package com.commonsense.android.kotlin.system.extensions

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
import android.view.Display
import android.view.WindowManager
import android.widget.Toast
import com.commonsense.android.kotlin.system.DangerousPermissionString
import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.system.logging.tryAndLog
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.net.URLEncoder


/**
 * Created by Kasper Tvede on 06-12-2016.
 */

fun Context.checkPermission(@DangerousPermissionString permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.presentDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivitySafe(intent)
}

fun Context.showOnMaps(address: String) {
    val urlEncoded = address.urlEncoded()
    val toLaunch = Uri.parse("geo:0,0?q=$urlEncoded")
    val intent = Intent(Intent.ACTION_VIEW, toLaunch)
    startActivitySafe(intent)
}


//fun Context.composeEmail(recipient : String, subject : String, message : String) {
//
//}

private fun String.urlEncoded(): String {
    return URLEncoder.encode(this, "UTF-8")
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


fun Context.getColorSafe(@ColorRes color: Int) = ContextCompat.getColor(this, color)


fun Context.getVirtualScreenSize(): Point? {
    val display = windowManager?.defaultDisplay
    return display?.let {
        val size = Point()
        display.getSize(size)
        return size
    }
}


val Context.windowManager: WindowManager?
    get() = getSystemService(Context.WINDOW_SERVICE) as? WindowManager


val Context.defaultDisplay: Display?
    get() = windowManager?.defaultDisplay

fun Context.getTypedArrayFor(attributeSet: AttributeSet,
                             @StyleableRes style: IntArray,
                             defStyleAttr: Int = 0,
                             defStyleRes: Int = 0): TypedArray =
        theme.obtainStyledAttributes(attributeSet, style, defStyleAttr, defStyleRes)

fun Context.startActivitySafe(intent: Intent) = tryAndLog("ContextExtensions") {
    startActivity(intent)
}