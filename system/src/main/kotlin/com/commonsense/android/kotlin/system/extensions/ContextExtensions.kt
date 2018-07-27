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
import android.view.*
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

@Suppress("NOTHING_TO_INLINE")
inline fun Context.checkPermission(@DangerousPermissionString permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.presentDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivitySafe(intent)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.showOnMaps(address: String) {
    val urlEncoded = address.urlEncoded()
    val toLaunch = Uri.parse("geo:0,0?q=$urlEncoded")
    val intent = Intent(Intent.ACTION_VIEW, toLaunch)
    startActivitySafe(intent)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.getDrawableSafe(@DrawableRes drawable: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawable)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.safeToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(this, message, length).show()
    } catch (e: Exception) {
        L.error("Activity.safeToast", "failed to show toast", e)
    }
}


@Suppress("NOTHING_TO_INLINE")
inline fun Context.safeToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    launch(UI) {
        try {
            Toast.makeText(this@safeToast, message, length).show()
        } catch (e: Exception) {
            L.error("Activity.safeToast", "failed to show toast", e)
        }
    }
}


@Suppress("NOTHING_TO_INLINE")
inline fun Context.getColorSafe(@ColorRes color: Int) =
        ContextCompat.getColor(this, color)


@Suppress("NOTHING_TO_INLINE")
inline fun Context.getVirtualScreenSize(): Point? {
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

@Suppress("NOTHING_TO_INLINE")
inline fun Context.startActivitySafe(intent: Intent) = tryAndLog("ContextExtensions") {
    startActivity(intent)
}