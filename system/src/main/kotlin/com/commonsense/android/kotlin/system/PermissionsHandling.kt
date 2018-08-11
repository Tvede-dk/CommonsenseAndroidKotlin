package com.commonsense.android.kotlin.system

import android.Manifest
import android.annotation.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.base.extensions.launchBlock
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.extensions.checkPermission
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI

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
 * Telss if the given int is the PackageManager's way of saying "Is granted"
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

/**
 * This is a collection of "dangerous permissions" thus requiring user allowance.
 * https://developer.android.com/guide/topics/permissions/requesting.html#permission-groups
 */
enum class PermissionEnum(@DangerousPermissionString val permissionValue: String) {

    //calendar
    ReadCalendar(Manifest.permission.READ_CALENDAR),
    WriteCalendar(Manifest.permission.WRITE_CALENDAR),

    // camera
    Camera(Manifest.permission.CAMERA),

    // contacts
    ReadContacts(Manifest.permission.READ_CONTACTS),
    WriteContacts(Manifest.permission.WRITE_CONTACTS),
    GetAccounts(Manifest.permission.GET_ACCOUNTS),
    //location
    FineLocation(Manifest.permission.ACCESS_FINE_LOCATION),
    CoarseLocation(Manifest.permission.ACCESS_COARSE_LOCATION),
    //microphone
    RecordAudio(Manifest.permission.RECORD_AUDIO),

    //Phone
    ReadPhoneState(Manifest.permission.READ_PHONE_STATE),
    MakeCall(Manifest.permission.CALL_PHONE),
    ReadCallLog(Manifest.permission.READ_CALL_LOG),
    WriteCallLog(Manifest.permission.WRITE_CALL_LOG),
    AddVoiceMail(Manifest.permission.ADD_VOICEMAIL),
    UseSIP(Manifest.permission.USE_SIP),
    ProcessOutgoingCalls(Manifest.permission.PROCESS_OUTGOING_CALLS),

    //Sensors
    @RequiresApi(VERSION_CODES.KITKAT_WATCH)
    BodySensors(Manifest.permission.BODY_SENSORS),
    // SMS
    SendSms(Manifest.permission.SEND_SMS),
    ReceiveSms(Manifest.permission.RECEIVE_SMS),
    ReadSms(Manifest.permission.READ_SMS),
    ReceiveWap(Manifest.permission.RECEIVE_WAP_PUSH),
    ReceiveMms(Manifest.permission.RECEIVE_MMS),

    //storage rights
    ReadExternalStorage(Manifest.permission.READ_EXTERNAL_STORAGE),
    WriteExternalStorage(Manifest.permission.WRITE_EXTERNAL_STORAGE),


}

@UiThread
fun PermissionEnum.useIfPermitted(context: Context,
                                  usePermission: EmptyFunction,
                                  useError: EmptyFunction) {
    havePermission(context)
            .ifTrue(usePermission)
            .ifFalse(useError)
}

@UiThread
fun PermissionEnum.use(handler: PermissionsHandling,
                       activity: Activity,
                       function: EmptyFunction,
                       errorFunction: EmptyFunction) {
    handler.performActionForPermission(
            permissionValue,
            activity,
            function,
            errorFunction)
}

@UiThread
fun PermissionEnum.useSuspend(handler: PermissionsHandling,
                              activity: Activity,
                              function: AsyncEmptyFunction,
                              errorFunction: AsyncEmptyFunction) {
    handler.performActionForPermission(permissionValue, activity, {
        launchBlock(UI, block = function)
    }, {
        launchBlock(UI, block = errorFunction)
    })
}

@UiThread
fun PermissionEnum.useSuspend(handler: PermissionsHandling,
                              activity: BaseActivity,
                              function: AsyncEmptyFunction,
                              errorFunction: AsyncEmptyFunction) {
    handler.performActionForPermission(permissionValue, activity, {
        activity.launchInUi("PermissionEnum.useSuspend", function)
    }, {
        activity.launchInUi("PermissionEnum.useSuspend", errorFunction)
    })
}


@UiThread
inline fun PermissionEnum.use(context: Context, crossinline usePermission: EmptyFunction) {
    havePermission(context).ifTrue(usePermission)
}

@UiThread
fun PermissionEnum.useSuspend(context: Context, usePermission: AsyncEmptyFunction): Job? {
    return if (havePermission(context)) {
        launchBlock(UI, block = usePermission)
    } else {
        null
    }
}

@UiThread
fun PermissionEnum.useSuspend(context: BaseActivity, usePermission: AsyncEmptyFunction) {
    if (havePermission(context)) {
        context.launchInUi("PermissionEnum.useSuspend", usePermission)
    }
}


@UiThread
fun PermissionEnum.havePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permissionValue).isGranted()
}

@UiThread
fun BaseActivity.use(permission: PermissionEnum,
                     usePermission: EmptyFunction,
                     onFailed: EmptyFunction? = null) {
    permissionHandler.performActionForPermission(
            permission.permissionValue,
            this,
            usePermission,
            onFailed
                    ?: {})
}

@UiThread
fun BaseActivity.askAndUsePermission(permission: PermissionEnum,
                                     usePermission: EmptyFunction) {
    permissionHandler.performActionForPermission(permission.permissionValue,
            this,
            onGranted = usePermission,
            onFailed = {})
}

/**
 * annotates all the dangerous permissions strings
 */
@SuppressLint("InlinedApi")
@Retention(AnnotationRetention.SOURCE)
@StringDef(Manifest.permission.ACCESS_CHECKIN_PROPERTIES, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        , Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_NOTIFICATION_POLICY
        , Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.CAMERA
        , Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECORD_AUDIO
        , Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG
        , Manifest.permission.ADD_VOICEMAIL, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.SEND_SMS
        , Manifest.permission.BODY_SENSORS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS
        , Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
)
annotation class DangerousPermissionString
