package com.commonsense.android.kotlin.views.extensions

import android.graphics.Rect
import android.view.ViewGroup

/**
 * Created by Kasper Tvede on 27-08-2017.
 */

/**
 * The width inside (after subtracting the padding).
 */
val ViewGroup.contentWidth
    get() = (measuredWidth - paddingRight) - paddingLeft

/**
 * The height inside (after subtracting the padding).
 */
val ViewGroup.contentHeight
    get() = (measuredHeight - paddingTop) - paddingBottom

/**
 * The inner container regarding padding.
 */
fun ViewGroup.contentSize(rectToUse: Rect) {
    rectToUse.set(paddingLeft, paddingTop, measuredWidth - paddingRight, measuredHeight - paddingBottom)
}
