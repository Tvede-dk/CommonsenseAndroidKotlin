package com.commonsense.android.kotlin.views.extensions

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.StyleableRes
import android.support.annotation.UiThread
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.AsyncFunctionUnit
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.forEachNotNull
import com.commonsense.android.kotlin.base.extensions.collections.ifFalse
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.extensions.getTypedArrayFor
import com.commonsense.android.kotlin.system.extensions.isApiEqualOrGreater
import com.commonsense.android.kotlin.system.logging.tryAndLog
import com.commonsense.android.kotlin.system.logging.tryAndLogSuspend
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
fun View.setOnclickAsyncSuspend(action: AsyncFunctionUnit<Context>) {
    val eventActor = actor<Unit>(UI, capacity = Channel.CONFLATED) {
        channel.consumeEach {
            val cont = getContext() ?: return@actor
            tryAndLogSuspend("onclickAsyncSuspend") {
                action(cont)
            }
        }
    }
    setOnClick { eventActor.offer(Unit) }
}

@UiThread
fun View.setOnclickAsyncSuspendEmpty(action: AsyncEmptyFunction) {
    this.setOnclickAsyncSuspend({ _ -> action() })
}

@UiThread
fun View.setOnclickAsync(action: FunctionUnit<Context>) {
    val eventActor = actor<Unit>(UI, capacity = Channel.CONFLATED) {
        channel.consumeEach {
            val cont = getContext() ?: return@actor
            tryAndLog("onclickAsync") {
                action(cont)
            }
        }
    }
    setOnClick { eventActor.offer(Unit) }
}

@UiThread
fun View.setOnclickAsyncEmpty(action: EmptyFunction) {
    this.setOnclickAsync({ _ -> action() })
}


fun View.getTypedArrayFor(attributeSet: AttributeSet,
                          @StyleableRes style: IntArray,
                          defStyleAttr: Int = 0,
                          defStyleRes: Int = 0): TypedArray =
        context.getTypedArrayFor(attributeSet, style, defStyleAttr, defStyleRes)


/**
 * resets all transformations on a view (x, y, z)
 */
fun View.resetTransformations() {
    translationX = 0f
    @SuppressLint("NewApi")
    if (isApiEqualOrGreater(21)) {
        translationZ = 0f
    }
    translationY = 0f
}


/**
 * Toggles between visible and gone.
 */
fun View.toggleVisibilityGone() {
    isVisible.ifTrue(this::gone).ifFalse(this::visible)
}

/**
 * Returns true iff its visible, false otherwise.
 */
val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

/**
 * returns true iff its gone otherwise false
 */
val View.isGone: Boolean
    get() = visibility == View.GONE

/**
 * returns true iff its invisible, otherwise false
 */
val View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE


fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleOrGone(condition: Boolean) {
    if (condition) {
        visible()
    } else {
        gone()
    }
}

fun Array<View?>.goneViews() {
    ViewHelper.goneViews(*this)
}

fun List<View?>.goneViews() {
    ViewHelper.goneViews(this)
}

fun List<View?>.visibleViews() {
    ViewHelper.showViews(this)
}


object ViewHelper {

    fun goneViews(vararg views: View?) {
        views.forEach { it?.gone() }
    }

    fun goneViews(views: Iterable<View?>) {
        views.forEachNotNull(View::gone)
    }

    fun showGoneView(toShow: View?, toGone: View?) {
        toShow?.visible()
        toGone?.gone()
    }

    fun showViews(views: Iterable<View?>) {
        views.forEachNotNull(View::visible)
    }
}

/**
 * Computes the children as a list.
 * instead of the old "0 to childCount".
 */
val ViewGroup.children: List<View>
    get() {
        return (0 until childCount).map(this::getChildAt)
    }


val ViewGroup.visibleChildren: List<View>
    get() = children.filterNot { it.isGone }

val ViewGroup.visibleChildrenCount: Int
    get() = visibleChildren.size


fun View.disable() {
    isEnabled = false
    isClickable = false
    (this as? ViewGroup)?.children?.forEach(View::disable)
}

fun View.enable() {
    isEnabled = true
    isClickable = true
    (this as? ViewGroup)?.children?.forEach(View::enable)
}


fun EditText.imeDone() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    inputType = inputType xor InputType.TYPE_TEXT_FLAG_MULTI_LINE xor InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
}