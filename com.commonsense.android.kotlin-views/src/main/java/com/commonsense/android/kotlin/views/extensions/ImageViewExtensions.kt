package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.ColorInt
import android.widget.ImageView
import com.commonsense.android.kotlin.system.imaging.withColor

/**
 * Created by Kasper Tvede on 24-07-2017.
 */

fun ImageView.colorOverlay(@ColorInt color: Int) {
    drawable?.withColor(color)?.let(this::setImageDrawable)
}

