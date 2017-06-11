package com.commonsense.android.kotlin.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.annotation.IntRange
import android.support.annotation.StringDef
import android.support.v4.app.ActivityCompat
import com.commonsense.android.kotlin.android.extensions.checkPermission
import com.commonsense.android.kotlin.extensions.collections.findAndRemoveAll
import onFalse
import onTrue

/**
 * Created by Kasper Tvede on 06-12-2016.
 */

data class PermissionRequest(@DangerousPermissionString val permission: String, val onGranted: () -> Unit, val onFailed: () -> Unit)

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
            requests.forEach { isGranted.onTrue(it.onGranted).onFalse(it.onFailed) }
        }
    }

    private fun Int.isGranted(): Boolean {
        return this == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(@DangerousPermissionString permission: String, activity: Activity) {
        activity.checkPermission(permission).onFalse {
            requestPermissionFor(permission, activity, { }, { })
        }
    }
}

enum class PermissionEnum(val permissionValue: String) {

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
