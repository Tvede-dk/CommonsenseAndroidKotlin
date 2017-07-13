package com.commonsense.android.kotlin.android.image

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt

/**
 * Created by kasper on 13/07/2017.
 */

fun Drawable.withColor(@ColorInt color: Int): Drawable? {
    return mutate().apply {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}