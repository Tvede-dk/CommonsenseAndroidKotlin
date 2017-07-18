package com.commonsense.android.kotlin.views.extensions

import android.annotation.TargetApi
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.StyleableRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import com.commonsense.android.kotlin.android.extensions.getTypedArrayFor
import com.commonsense.android.kotlin.base.extensions.tryAndLog
import com.commonsense.android.kotlin.base.extensions.tryAndLogSuspend
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
fun View.setOnclickAsyncSuspend(action: suspend () -> Unit) {
    val eventActor = actor<Unit>(UI, capacity = Channel.CONFLATED) {
        channel.consumeEach { tryAndLogSuspend("onclickAsyncSuspend", action) }
    }
    setOnClick { eventActor.offer(Unit) }
}

@UiThread
fun View.setOnclickAsync(action: () -> Unit) {
    val eventActor = actor<Unit>(UI, capacity = Channel.CONFLATED) {
        channel.consumeEach {
            tryAndLog("onclickAsync", action)
        }
    }
    setOnClick { eventActor.offer(Unit) }
}


fun View.getTypedArrayFor(attributeSet: AttributeSet,
                          @StyleableRes style: IntArray,
                          defStyleAttr: Int = 0,
                          defStyleRes: Int = 0): TypedArray =
        context.getTypedArrayFor(attributeSet, style, defStyleAttr, defStyleRes)


/**
 * Toggles between visible and gone.
 */
fun View.toggleVisibilityGone() {
    if (isVisible) {
        gone()
    } else {
        visible()
    }
}

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

val View.isGone: Boolean
    get() = visibility == View.GONE

val View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE


object ViewHelper {

    fun goneViews(vararg views: View?) {
        views.forEach { it?.gone() }
    }

    fun goneViews(views: Iterable<View?>) {
        views.forEach { it?.gone() }
    }

    fun showGoneView(toShow: View?, toGone: View?) {
        toShow?.visible()
        goneViews(listOf(toGone))
    }
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun Array<View?>.goneViews() {
    ViewHelper.goneViews(*this)
}

fun List<View?>.goneViews() {
    ViewHelper.goneViews(this)
}
