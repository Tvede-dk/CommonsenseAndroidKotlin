package com.commonsense.android.kotlin.android.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.widget.Toast
import com.commonsense.android.kotlin.android.DangerousPermissionString
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.kotlin.R

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

fun Context.getDrawableSafe(@DrawableRes drawable: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawable)
}

fun Context.safeToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(this, message, length).show()
    } catch (e: Exception) {
        L.error("Activity.safeToast", "failed to show toast", e)
    }
}


fun Context.safeToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(this, message, length).show()
    } catch (e: Exception) {
        L.error("Activity.safeToast", "failed to show toast", e)
    }
}


fun Context.startUrl(url: String, forceHttps: Boolean = true, useInbuildBrowser: Boolean = true) {
    val isHttp = url.startsWith("http://")
    val isHttps = url.startsWith("https://")

    val safeUrl = when {
        !isHttp && !isHttps -> "https://" + url
        isHttp && forceHttps -> url.replace("http://", "https://")
        isHttp && !forceHttps -> url
        isHttps -> url
        else -> url
    }

    val toStart = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl))
    try {
        startActivity(toStart)
    } catch (notFound: ActivityNotFoundException) {
        L.error("ActivityExtensions", "Activity not found to launch url:$safeUrl", notFound)
        if (useInbuildBrowser) {

        } else {
            safeToast(R.string.missing_browser, Toast.LENGTH_SHORT)
        }
        //TODO present inbuild webview.
    }
}


fun Context.getColorSafe(@ColorRes color: Int) = ContextCompat.getColor(this, color)