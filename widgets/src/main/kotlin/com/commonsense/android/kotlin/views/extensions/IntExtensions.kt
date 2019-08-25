@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.graphics.*
import android.support.annotation.AnyThread
import android.support.annotation.ColorInt
import kotlin.math.roundToInt

/**
 * Given a color in an int, creates a darker version of it by changing all component hereof.
 * @receiver Int
 * @param factor Float the factor to apply to each color (if 1 then the color would not change, if 0 all color components would become 0)
 * @return Int the darker color
 */
@AnyThread
@Suppress("NOTHING_TO_INLINE")
@ColorInt
inline fun Int.darkenColor(factor: Float = 0.7f): Int {
    val a = Color.alpha(this)
    val r = (Color.red(this) * factor).roundToInt()
    val g = (Color.green(this) * factor).roundToInt()
    val b = (Color.blue(this) * factor).roundToInt()
    @Suppress("NamedArgsPositionMismatch")
    return Color.argb(a,
            Math.min(r, 255),
            Math.min(g, 255),
            Math.min(b, 255))
}