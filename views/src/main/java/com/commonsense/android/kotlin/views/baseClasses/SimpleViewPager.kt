package com.commonsense.android.kotlin.views.baseClasses


import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.MotionEvent

/**
 * Created by kasper on 17/07/2017.
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