package com.commonsense.android.kotlin.android.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import com.commonsense.android.kotlin.android.DangerousPermissionString

/**
 * Created by Kasper Tvede on 06-12-2016.
 */

fun Context.checkPermission(@DangerousPermissionString permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.PresentDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivity(intent)
}