package com.commonsense.android.kotlin.system.imaging

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * Created by kasper on 13/07/2017.
 */

/**
 *
 */
fun Drawable.withColor(@ColorInt color: Int): Drawable? {
    return mutate().apply {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

/**
 *
 */
fun Drawable.withTintColor(@ColorInt color: Int): Drawable? {
    return mutate().apply {
        DrawableCompat.setTint(this, color)
    }
}