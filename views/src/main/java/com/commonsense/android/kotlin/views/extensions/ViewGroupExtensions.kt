package com.commonsense.android.kotlin.views.extensions

import android.graphics.Rect
import android.support.annotation.IntRange
import android.view.View
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

/**
 *
 */
fun ViewGroup.addViews(views: List<View>) {
    views.forEach(this::addView)
}

/**
 *
 */
fun ViewGroup.addViews(views: List<View>, @IntRange(from = 0) atIndex: Int) {
    views.forEachIndexed { index, view ->
        this.addView(view, index + atIndex)
    }
}
