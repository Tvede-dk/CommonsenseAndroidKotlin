package com.commonsense.android.kotlin.android.extensions.widets

import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.UiThread
import android.view.View
import android.view.ViewTreeObserver


/**
 * Created by Kasper Tvede on 29-10-2016.
 */

@UiThread
inline fun View.setOnClick(crossinline listener: () -> Unit) {
    setOnClickListener { listener() }
}

@UiThread
inline fun View.setOnClickView(crossinline listener: (View) -> Unit) {
    setOnClickListener { listener(it) }
}


@UiThread
inline fun View.measureSize(crossinline afterMeasureAction: (with: Int, height: Int) -> Unit) {
    if (!viewTreeObserver.isAlive) {
        return
    }
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListenerCompact(this)
            afterMeasureAction(width, height)
        }
    })
}

@UiThread
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun ViewTreeObserver.removeOnGlobalLayoutListenerCompact(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    removeOnGlobalLayoutListener(listener)
}