package com.commonsense.android.kotlin.android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.annotation.IntRange
import android.support.annotation.MainThread
import android.support.annotation.StringDef
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.commonsense.android.kotlin.android.extensions.checkPermission
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import com.commonsense.android.kotlin.extensions.collections.findAndRemoveAll
import ifFalse
import ifTrue
import onFalse
import onTrue

/**
 * Created by Kasper Tvede on 06-12-2016.
 */

data class PermissionRequest(@DangerousPermissionString val permission: String, val onGranted: () -> Unit, val onFailed: () -> Unit)

private fun Int.isGranted(): Boolean {
    return this == PackageManager.PERMISSION_GRANTED
}


class PermissionsHandling(val handlerRequestCode: Int = 999) {


    val requestsInFlight = mutableListOf<PermissionRequest>()

    fun performActionForPermission(@DangerousPermissionString permission: String, activity: Activity, onGranted: () -> Unit, onFailed: () -> Unit) {
        activity.checkPermission(permission)
                .onTrue(onGranted)
                .onFalse {
                    requestPermissionFor(permission, activity, onGranted, onFailed)
                }
    }

    private fun requestPermissionFor(@DangerousPermissionString permission: String, activity: Activity, onGranted: () -> Unit, onFailed: () -> Unit) {
        val anyRequests = requestsInFlight.isEmpty()
        requestsInFlight.add(PermissionRequest(permission, onGranted, onFailed))
        anyRequests.onTrue { ActivityCompat.requestPermissions(activity, arrayOf(permission), handlerRequestCode) }
    }

    /**
     *
     * @return true if handled, false otherwise.
     */
    fun onRequestPermissionResult(@IntRange(from = 0) requestCode: Int, permissions: Array<out String>, grantedResults: IntArray): Boolean {
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

@MainThread
fun PermissionEnum.useIfPermitted(context: Context, usePermission: () -> Unit, useError: () -> Unit) {
    havePermission(context).ifTrue(usePermission).ifFalse(useError)
}

@MainThread
fun PermissionEnum.use(handler: PermissionsHandling, activity: Activity, function: () -> Unit, errorFunction: () -> Unit) {
    handler.performActionForPermission(permissionValue, activity, function, errorFunction)
}

@MainThread
inline fun PermissionEnum.use(context: Context, crossinline usePermission: () -> Unit) {
    havePermission(context).ifTrue(usePermission)
}

@MainThread
fun PermissionEnum.havePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permissionValue).isGranted()
}

@MainThread
fun BaseActivity.use(permission: PermissionEnum, usePermission: () -> Unit, onFailed: (() -> Unit)? = null) {
    permissionHandler.performActionForPermission(permission.permissionValue, this, usePermission, onFailed ?: {})
}


/**
 * annotates all the dangerous permissions strings...
 */
@Retention(AnnotationRetention.SOURCE)
@StringDef(Manifest.permission.ACCESS_CHECKIN_PROPERTIES, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        , Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_NOTIFICATION_POLICY
        , Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.CAMERA
        , Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECORD_AUDIO
        , Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG
        , Manifest.permission.ADD_VOICEMAIL, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.SEND_SMS
        , Manifest.permission.BODY_SENSORS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS
        , Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
) annotation class DangerousPermissionString
