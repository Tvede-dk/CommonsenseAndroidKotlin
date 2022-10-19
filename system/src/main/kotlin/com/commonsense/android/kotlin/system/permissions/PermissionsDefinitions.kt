package com.commonsense.android.kotlin.system.permissions

import android.*
import android.annotation.*
import android.os.*
import androidx.annotation.*

/**
 * annotates all the dangerous permissions strings
 */


/**
 * This is a list of all Dangerous permissions.
 * taken from https://developer.android.com/reference/android/Manifest.permission (and then selecting those with "dangerous" as the protection level)
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
@SuppressLint("InlinedApi")
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    Manifest.permission.ACCESS_CHECKIN_PROPERTIES,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.WRITE_CALENDAR,
    Manifest.permission.CAMERA,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.WRITE_CONTACTS,
    Manifest.permission.GET_ACCOUNTS,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.CALL_PHONE,
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.WRITE_CALL_LOG,
    Manifest.permission.ADD_VOICEMAIL,
    Manifest.permission.USE_SIP,
    Manifest.permission.PROCESS_OUTGOING_CALLS,
    Manifest.permission.SEND_SMS,
    Manifest.permission.BODY_SENSORS,
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.READ_SMS,
    Manifest.permission.RECEIVE_WAP_PUSH,
    Manifest.permission.RECEIVE_MMS,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,


    Manifest.permission.ACCEPT_HANDOVER,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.ACCESS_MEDIA_LOCATION,
    Manifest.permission.ACTIVITY_RECOGNITION,
    Manifest.permission.ANSWER_PHONE_CALLS,
    Manifest.permission.BLUETOOTH_ADVERTISE,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BODY_SENSORS_BACKGROUND,
    Manifest.permission.NEARBY_WIFI_DEVICES,
    Manifest.permission.POST_NOTIFICATIONS,
    Manifest.permission.READ_BASIC_PHONE_STATE,

    Manifest.permission.READ_MEDIA_AUDIO,
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_VIDEO,
    Manifest.permission.READ_PHONE_NUMBERS,

    )
annotation class DangerousPermissionString


//TODO inline this when available.
/*
* This is a collection of "dangerous permissions" thus requiring user allowance.
* https://developer.android.com/guide/topics/permissions/requesting.html#permission-groups
*/
enum class PermissionEnum(@DangerousPermissionString val permissionValue: String) {

    //calendar
    ReadCalendar(Manifest.permission.READ_CALENDAR),
    WriteCalendar(Manifest.permission.WRITE_CALENDAR),


    //<editor-fold desc="Media / camera">
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    ReadMediaAudio(Manifest.permission.READ_MEDIA_AUDIO),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    ReadMediaImages(Manifest.permission.READ_MEDIA_IMAGES),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    ReadMediaVideo(Manifest.permission.READ_MEDIA_VIDEO),
    RecordAudio(Manifest.permission.RECORD_AUDIO),
    //</editor-fold>


    Camera(Manifest.permission.CAMERA),

    // contacts
    ReadContacts(Manifest.permission.READ_CONTACTS),
    WriteContacts(Manifest.permission.WRITE_CONTACTS),
    GetAccounts(Manifest.permission.GET_ACCOUNTS),

    //<editor-fold desc="Location">
    FineLocation(Manifest.permission.ACCESS_FINE_LOCATION),
    CoarseLocation(Manifest.permission.ACCESS_COARSE_LOCATION),

    @RequiresApi(Build.VERSION_CODES.Q)
    BackgroundLocation(Manifest.permission.ACCESS_BACKGROUND_LOCATION),

    @RequiresApi(Build.VERSION_CODES.Q)
    MediaLocation(Manifest.permission.ACCESS_MEDIA_LOCATION),
    //</editor-fold>

    ReadPhoneState(Manifest.permission.READ_PHONE_STATE),
    MakeCall(Manifest.permission.CALL_PHONE),
    ReadCallLog(Manifest.permission.READ_CALL_LOG),
    WriteCallLog(Manifest.permission.WRITE_CALL_LOG),
    AddVoiceMail(Manifest.permission.ADD_VOICEMAIL),
    UseSIP(Manifest.permission.USE_SIP),
    ProcessOutgoingCalls(Manifest.permission.PROCESS_OUTGOING_CALLS),


    @RequiresApi(Build.VERSION_CODES.Q)
    ActivityRecognition(Manifest.permission.ACTIVITY_RECOGNITION),


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NearbyWifiDevices(Manifest.permission.NEARBY_WIFI_DEVICES),


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    PostNotifications(Manifest.permission.POST_NOTIFICATIONS),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    ReadBasicPhoneState(Manifest.permission.READ_BASIC_PHONE_STATE),


    @RequiresApi(Build.VERSION_CODES.O)
    ReadPhoneNumbers(Manifest.permission.READ_PHONE_NUMBERS),

    @RequiresApi(Build.VERSION_CODES.O)
    AnswerPhoneCalls(Manifest.permission.ANSWER_PHONE_CALLS),

    @RequiresApi(Build.VERSION_CODES.P)
    AcceptHandOver(Manifest.permission.ACCEPT_HANDOVER),
    //</editor-fold>


    //Sensors
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    BodySensors(Manifest.permission.BODY_SENSORS),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    BodySensorsBackground(Manifest.permission.BODY_SENSORS_BACKGROUND),

    // SMS
    SendSms(Manifest.permission.SEND_SMS),
    ReceiveSms(Manifest.permission.RECEIVE_SMS),
    ReadSms(Manifest.permission.READ_SMS),
    ReceiveWap(Manifest.permission.RECEIVE_WAP_PUSH),
    ReceiveMms(Manifest.permission.RECEIVE_MMS),

    //storage rights
    ReadExternalStorage(Manifest.permission.READ_EXTERNAL_STORAGE),
    WriteExternalStorage(Manifest.permission.WRITE_EXTERNAL_STORAGE),


    //<editor-fold desc="bluetooth">
    Bluetooth(Manifest.permission.BLUETOOTH),

    @RequiresApi(Build.VERSION_CODES.S)
    BluetoothConnect(Manifest.permission.BLUETOOTH_CONNECT),

    @RequiresApi(Build.VERSION_CODES.S)
    BluetoothAdvertise(Manifest.permission.BLUETOOTH_ADVERTISE),

    @RequiresApi(Build.VERSION_CODES.S)
    BluetoothScan(Manifest.permission.BLUETOOTH_SCAN),

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    BluetoothPrivileged(Manifest.permission.BLUETOOTH_PRIVILEGED),
    //</editor-fold>

}
