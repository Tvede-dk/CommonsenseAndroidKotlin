package com.commonsense.android.kotlin.android.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.annotation.StyleableRes
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.Toast
import com.commonsense.android.kotlin.android.DangerousPermissionString
import com.commonsense.android.kotlin.android.activities.InbuiltWebview
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.kotlin.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


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
    launch(UI) {
        try {
            Toast.makeText(this@safeToast, message, length).show()
        } catch (e: Exception) {
            L.error("Activity.safeToast", "failed to show toast", e)
        }
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
            InbuiltWebview.showUrl(safeUrl, this)
        } else {
            safeToast(R.string.missing_browser, Toast.LENGTH_SHORT)
        }
    }
}


fun Context.getColorSafe(@ColorRes color: Int) = ContextCompat.getColor(this, color)


fun Context.GetVirtualScreenSize(): Point? {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    val display = windowManager?.defaultDisplay
    return display?.let {
        val size = Point()
        display.getSize(size)
        return size
    }
}


fun Context.getTypedArrayFor(attributeSet: AttributeSet,
                             @StyleableRes style: IntArray,
                             defStyleAttr: Int = 0,
                             defStyleRes: Int = 0): TypedArray =
        theme.obtainStyledAttributes(attributeSet, style, defStyleAttr, defStyleRes)