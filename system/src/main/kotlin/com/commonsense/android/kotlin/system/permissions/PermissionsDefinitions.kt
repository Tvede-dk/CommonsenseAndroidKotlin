package com.commonsense.android.kotlin.system.permissions

import android.*
import android.annotation.*
import android.os.*
import androidx.annotation.*

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




//TODO inline this when available.
/*
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
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
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
