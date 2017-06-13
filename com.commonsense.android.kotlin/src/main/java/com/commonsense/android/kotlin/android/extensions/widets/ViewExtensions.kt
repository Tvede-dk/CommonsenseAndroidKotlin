package com.commonsense.android.kotlin.android.extensions.widets

import android.annotation.TargetApi
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.StyleableRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach


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

@UiThread
fun View.setOnclickAsync(action: suspend () -> Unit) {
    // launch one actor
    val eventActor = actor<Unit>(UI, capacity = Channel.CONFLATED) {
        channel.consumeEach { action() }
    }
    // install a listener to activate this actor
    setOnClick { eventActor.offer(Unit) }
}


fun View.getTypedArrayFor(attributeSet: AttributeSet,
                          @StyleableRes style: IntArray,
                          defStyleAttr: Int = 0,
                          defStyleRes: Int = 0): TypedArray {
    return context.theme.obtainStyledAttributes(attributeSet, style, defStyleAttr, defStyleRes)
}