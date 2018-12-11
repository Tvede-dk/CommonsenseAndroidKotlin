@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.graphics.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.view.*

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
 * Adds all the given views to this viewgroup
 * @receiver ViewGroup the view to append all the given views to
 * @param views List<View> the views to add / append
 */
fun ViewGroup.addViews(views: List<View>) {
    views.forEach(this::addView)
}

/**
 * Adds / inserts the given views into this viewgroup.
 * @receiver ViewGroup the ViewGroup to insert into
 * @param views List<View> the views to insert
 * @param atIndex Int where to perform the insert must be 0 or greater
 */
fun ViewGroup.addViews(views: List<View>, @IntRange(from = 0) atIndex: Int) {
    views.forEachIndexed { index, view ->
        this.addView(view, index + atIndex)
    }
}



/**
 * Computes the children as a list.
 * instead of the old "0 to childCount".
 * This is O(n) where n being the number of children
 */
val ViewGroup.children: List<View>
    @UiThread
    get() {
        return (0 until childCount).map(this::getChildAt)
    }


/**
 * Computes all the visible children;
 * (this includes invisible as they participate in the layout thus are not truly invisible)
 * this is O(n) where n being the number of children.
 */
val ViewGroup.visibleChildren: List<View>
    @UiThread
    get() = children.filterNot { it.isGone }

/**
 * Counts the number of visible children;
 * warning is is O(n) (n being children)
 */
val ViewGroup.visibleChildrenCount: Int
    @UiThread
    get() = visibleChildren.size
