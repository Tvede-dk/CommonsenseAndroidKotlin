package com.commonsense.android.kotlin.extensions

import android.graphics.Color
import android.support.annotation.ColorInt

/**
 * Created by kasper on 13/07/2017.
 */


@ColorInt
fun Int.darkenColor(factor: Float = 0.7f): Int {
    val a = Color.alpha(this)
    val r = Math.round(Color.red(this) * factor)
    val g = Math.round(Color.green(this) * factor)
    val b = Math.round(Color.blue(this) * factor)
    return Color.argb(a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255))
}