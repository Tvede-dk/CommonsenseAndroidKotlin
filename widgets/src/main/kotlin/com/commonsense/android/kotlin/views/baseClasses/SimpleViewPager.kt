@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.baseClasses


import android.annotation.*
import android.view.*
import androidx.viewpager.widget.*

/**
 * Created by Kasper Tvede
 * TODO expand this.
 */
class SimpleViewPager : ViewPager {

    var isSwipeAllowed: Boolean = true

    constructor(context: android.content.Context) : super(context) {
        afterInit()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {
        afterInit()
    }

    private fun afterInit() {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isSwipeAllowed) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!isSwipeAllowed) {
            return false
        }
        return super.onTouchEvent(ev)
    }

    override fun executeKeyEvent(event: KeyEvent): Boolean {
        if (!isSwipeAllowed) {
            return false
        }
        return super.executeKeyEvent(event)
    }

}