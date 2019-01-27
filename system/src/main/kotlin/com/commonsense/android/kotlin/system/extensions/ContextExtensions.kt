@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.app.*
import android.content.*
import android.content.pm.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.net.*
import android.support.annotation.*
import android.support.v4.content.*
import android.support.v7.content.res.*
import android.util.*
import android.view.*
import android.widget.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.system.permissions.*
import kotlinx.coroutines.*


/**
 * Created by Kasper Tvede
 */

inline fun Context.checkPermission(@DangerousPermissionString permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

inline fun Context.getDrawableSafe(@DrawableRes drawable: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawable)
}

inline fun Context.safeToast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(this, message, length).show()
    } catch (e: Exception) {
        L.error("Activity.safeToast", "failed to show toast", e)
    }
}


inline fun Context.safeToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    GlobalScope.launch(Dispatchers.Main) {
        try {
            Toast.makeText(this@safeToast, message, length).show()
        } catch (e: Exception) {
            L.error("Activity.safeToast", "failed to show toast", e)
        }
    }
}


inline fun Context.getColorSafe(@ColorRes color: Int) =
        ContextCompat.getColor(this, color)


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

inline fun Context.startActivitySafe(intent: Intent) = tryAndLog("ContextExtensions") {
    startActivity(intent)
}

inline fun <T : Activity> Context.startActivity(clz: Class<T>): Unit =
        startActivity(Intent(this, clz))


inline fun <T : Service> Context.startService(clz: Class<T>): ComponentName? =
        startService(Intent(this, clz))
