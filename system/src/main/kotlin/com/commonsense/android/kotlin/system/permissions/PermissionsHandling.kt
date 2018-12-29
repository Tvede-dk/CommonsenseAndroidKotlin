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
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.extensions.*
import kotlinx.coroutines.*

/**
 * Created by Kasper Tvede
 * Handling of permissions.
 */

/**
 * The content of a permission request (wrapping the permission itself and the handlers)
 */
data class PermissionRequest(@DangerousPermissionString val permission: String,
                             val onGranted: EmptyFunction,
                             val onFailed: EmptyFunction)

/**
 * Tells if the given int is the PackageManager's way of saying "Is granted"
 */
internal fun Int.isGranted(): Boolean {
    return this == PackageManager.PERMISSION_GRANTED
}

/**
 *
 */
class PermissionsHandling(val handlerRequestCode: Int = 999) {

    /**
     *
     */
    private val requestsInFlight = mutableListOf<PermissionRequest>()

    /**
     *
     */
    fun performActionForPermission(@DangerousPermissionString permission: String,
                                   activity: Activity,
                                   onGranted: EmptyFunction,
                                   onFailed: EmptyFunction) {
        //fist if we have the permission just use it
        activity.checkPermission(permission)
                .onTrue(onGranted)
                .onFalse {
                    requestPermissionFor(permission, activity, onGranted, onFailed)
                }
    }

    private fun requestPermissionFor(@DangerousPermissionString permission: String,
                                     activity: Activity,
                                     onGranted: EmptyFunction,
                                     onFailed: EmptyFunction) {
        val anyRequests = requestsInFlight.isEmpty()
        requestsInFlight.add(PermissionRequest(permission, onGranted, onFailed))
        anyRequests.onTrue { ActivityCompat.requestPermissions(activity, arrayOf(permission), handlerRequestCode) }
    }

    /**
     *
     * @return true if handled, false otherwise.
     */
    fun onRequestPermissionResult(@IntRange(from = 0) requestCode: Int,
                                  permissions: Array<out String>,
                                  grantedResults: IntArray): Boolean {
        return (requestCode == handlerRequestCode).onTrue {
            val requests = requestsInFlight.findAndRemoveAll { it.permission == permissions.firstOrNull() }
            val isGranted = grantedResults.firstOrNull()?.isGranted() ?: false
            requests.forEach { isGranted.ifTrue(it.onGranted).ifFalse(it.onFailed) }
        }
    }


    fun requestPermissions(@DangerousPermissionString permission: String, activity: Activity) {
        activity.checkPermission(permission).onFalse {
            requestPermissionFor(permission, activity, { }, { })
        }
    }

    override fun toString(): String = toPrettyString()

    fun toPrettyString(): String {
        return "Permission handler state:" +
                requestsInFlight.map {
                    "${it.permission}," +
                            " with onGrant: ${it.onGranted}," +
                            " on failed: ${it.onFailed}"
                }.prettyStringContent("Permissions in flight:",
                        "no permissions in flight") +
                "\r\t" +
                "request code is $handlerRequestCode"
    }
}
