@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.commonsense.android.kotlin.system.permissions

import android.app.*
import android.content.*
import android.content.pm.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.v4.app.*
import android.support.v4.content.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*

/**
 * Handling of permissions. this class (and the supporting classes) basically handles the whole flow of permission requesting.
 */


//region callback types
/**
 * Tells that all request(s) went well and we have a success for all permission(s)
 */
typealias PermissionsSuccessCallback = (successfulPermissions: List<@DangerousPermissionString String>) -> Unit

/**
 * Tells that all (or some) requests failed. The successfull onces as well as the failed onces are supplied.
 */
typealias PermissionsFailedCallback = (successfulPermissions: List<@DangerousPermissionString String>, failedPermissions: List<@DangerousPermissionString String>) -> Unit

//endregion
/**
 * You can use this class to implement a queued design of permission requesting;
 * So if you are not using baseActivity, you can still have the permission design as if.
 *
 * SINCE ALL IS REQUIRED TO BE UI THREAD; WE SHOULD NOT HAVE ANY CONCURRENT MODIFICATIONS.
 * THIS IS NOT THREADSAFE !
 *
 * @property handlerRequestCode Int the request code that should be used for permission requests.
 * @property requestsInFlight MutableList<PermissionRequest> the in flight (pending) permission requests.
 */
class PermissionsHandling(
        val handlerRequestCode: Int = 999
) {

    /**
     * a list of all permissions pending.
     */
    private val requestsInFlight = mutableListOf<PermissionRequest>()

    //region public interface
    @UiThread
    fun performActionForPermissionsFull(permissions: List<@DangerousPermissionString String>,
                                        activity: Activity,
                                        @UiThread onGranted: PermissionsSuccessCallback,
                                        @UiThread onFailed: PermissionsFailedCallback?) {


        if (permissions.isAllGranted(activity)) {
            onGranted(permissions.toList())
        } else {
            requestPermissionFor(permissions, activity, onGranted, onFailed)
        }
    }

    @UiThread
    fun performActionForPermissions(permissions: List<@DangerousPermissionString String>,
                                    activity: Activity,
                                    @UiThread onGranted: EmptyFunction,
                                    @UiThread onFailed: PermissionsFailedCallback?) {
        if (permissions.isAllGranted(activity)) {
            onGranted() //optimize the amount of overhead for this call by not delegating to the other perform.
        } else {
            //only here do the "extra" wrapping / allocation for the onGranted.
            requestPermissionFor(permissions, activity, { onGranted() }, onFailed)
        }
    }


    @UiThread
    fun performActionForPermissionsEnumFull(permissions: List<PermissionEnum>,
                                            activity: Activity,
                                            @UiThread onGranted: PermissionsSuccessCallback,
                                            @UiThread onFailed: PermissionsFailedCallback?) =
            performActionForPermissionsFull(permissions.map { it.permissionValue }, activity, onGranted, onFailed)

    @UiThread
    fun performActionForPermissionsEnum(permissions: List<PermissionEnum>,
                                        activity: Activity,
                                        @UiThread onGranted: EmptyFunction,
                                        @UiThread onFailed: PermissionsFailedCallback?) =
            performActionForPermissions(permissions.map { it.permissionValue }, activity, onGranted, onFailed)

    //endregion

    /**
     * Computes if all the given permissions are granted or not.
     * @receiver List<@DangerousPermissionString String>
     * @param activity Activity
     * @return Boolean
     */
    private fun List<@DangerousPermissionString String>.isAllGranted(activity: Activity): Boolean =
            all { activity.checkPermission(it) } //is all true => is all granted.

    @UiThread
    private fun requestPermissionFor(permissions: List<@DangerousPermissionString String>,
                                     activity: Activity,
                                     @UiThread onGranted: PermissionsSuccessCallback,
                                     @UiThread onFailed: PermissionsFailedCallback?) {
        val permissionsArray = permissions.toTypedArray()
        val hadNonePending = requestsInFlight.isEmpty()
        requestsInFlight.add(PermissionRequest(permissionsArray, onGranted, onFailed))
        hadNonePending.ifTrue { requestPermissions(activity, permissionsArray) }
    }

    @UiThread
    private fun requestPermissions(
            activity: Activity,
            permissions: Array<out @DangerousPermissionString String>) {
        ActivityCompat.requestPermissions(activity, permissions, handlerRequestCode)
    }


    //region callback
    /**
     * The callback for the Activity method called "onRequestPermissionsResult"
     *
     * @param activity Activity so if there are pending permission requests we can fire them.
     * @param requestCode from the original callback
     * @param permissions from the original callback
     * @param grantedResults from the original callback
     * @return true if handled, false otherwise.
     */
    @UiThread
    fun onRequestPermissionResult(
            activity: Activity,
            @IntRange(from = 0) requestCode: Int,
            permissions: Array<out String>,
            grantedResults: IntArray): Boolean {
        return (requestCode == handlerRequestCode).onTrue {

            /*
             * From the documentation
                Note: It is possible that the permissions request interaction with the user is interrupted.
                    In this case you will receive empty permissions and results arrays which should be
                        treated as a cancellation.
             * Thus we are to handle this very weird scenario; so lets detect it first.
             * also if the permissions and granted are not equal this would seem like a cancellation.
             *
            */
            if (isCancellation(permissions, grantedResults)) {
                //treat as cancellation, so cancel the first in row. (it failed)
                requestsInFlight.removeAt(0).failed()
                //TODO if a cancellation is triggered, are we to continue queuing permission requests ?? this seems off
                // as a cancellation would mean that we are unable to ask for permissions.
                return@onTrue //break out
            }
            //compute all permissions that are successful and those that failed
            val (successful, failed) = computeSuccessAndFailed(permissions, grantedResults)

            //compute all requests that are to be notified (since we currently skip all not having the same amount of permissions.
            //so no subset computing (eg, request x,y, then request y, should also respond to y.
            val requests = requestsInFlight.findAndRemoveAll {
                it.permissions.contentEquals(permissions)
            }

            //if non failed => all is good
            if (failed.isEmpty()) {
                requests.onGranted(successful)
            } else {
                requests.onFailed(successful, failed)
            }
            //after all is done, see if any requests are remaining and if so, queue up the next.
            requestsInFlight.firstOrNull()?.let {
                requestPermissions(activity, it.permissions)
            }
        }
    }

    @UiThread
    private fun List<PermissionRequest>.onGranted(
            successful: List<@DangerousPermissionString String>) = forEach {
        it.onGranted(successful)
    }

    @UiThread
    private fun List<PermissionRequest>.onFailed(
            successfull: List<@DangerousPermissionString String>,
            failed: List<@DangerousPermissionString String>) = forEach {
        it.onFailed?.invoke(successfull, failed)
    }


//endregion

    //region printing / toString
    override fun toString(): String = toPrettyString()

    fun toPrettyString(): String {
        return "Permission handler state:" +
                requestsInFlight.map {
                    "${it.permissions}," +
                            " with onGrant: ${it.onGranted}," +
                            " on failed: ${it.onFailed}"
                }.prettyStringContent("Permissions in flight:",
                        "no permissions in flight") +
                "\r\t" +
                "request code is $handlerRequestCode"
    }

    companion object {
        /**
         * Tells if the given permissions and granted results appear to be a "cancellation" by the docs
         * @param permissions Array<out String>
         * @param grantedResults IntArray
         * @return Boolean true if it appears as a cancellation. false otherwise
         */
        fun isCancellation(permissions: Array<out String>,
                           grantedResults: IntArray) =
                permissions.isEmpty() || permissions.size != grantedResults.size

        /**
         * Computes from a raw result of permissions and granted results what permission are really
         * @param permissions Array<out String>
         * @param grantedResults IntArray
         *
         */
        fun computeSuccessAndFailed(
                permissions: Array<out String>,
                grantedResults: IntArray
        ): ComputeSuccessFailureResult {
            val successResult = mutableListOf<@DangerousPermissionString String>()
            val failedResult = mutableListOf<@DangerousPermissionString String>()
            permissions.forEachIndexed { index, permission ->
                val isGranted = grantedResults[index].isGranted()
                val addFunction: Function1<String, Boolean> = isGranted.map(successResult::add, failedResult::add)
                addFunction(permission)
            }
            return ComputeSuccessFailureResult(successResult, failedResult)
        }
    }
//endregion
}

/**
 * Models the result of computeSuccessAndFailed with names.
 *
 * Allows to be destructed.
 */
data class ComputeSuccessFailureResult(
        val successful: List<@DangerousPermissionString String>,
        val failed: List<@DangerousPermissionString String>
)

//region internal
/**
 * The content of a permission request (wrapping the permission itself and the handlers)
 */
private class PermissionRequest(val permissions: Array<out @DangerousPermissionString String>,
                                val onGranted: PermissionsSuccessCallback,
                                val onFailed: PermissionsFailedCallback?)

/**
 * Tells if the given int is the PackageManager's way of saying "Is granted"
 * its internal to avoid user code accidentally using it as it is an extension on INT:
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun Int.isGranted(): Boolean {
    return this == PackageManager.PERMISSION_GRANTED
}

/**
 * Simplifies the intention of a "failed" request. simply calls the on failed with all permissions as failed.
 */
private fun PermissionRequest.failed() {
    onFailed?.invoke(emptyList(), permissions.toList())
}

/**
 * Tells if we have the given permission (name) (
 * @param permissionName String see PermissionEnum for values.
 * @return Boolean true if the permission is granted
 */
@Suppress("RemoveRedundantQualifierName") //bug in idea.
fun Context.havePermission(permissionName: String): Boolean =
        ContextCompat.checkSelfPermission(this, permissionName).isGranted()

//endregion