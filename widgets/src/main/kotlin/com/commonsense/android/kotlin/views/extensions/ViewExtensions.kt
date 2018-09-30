@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.content.*
import android.content.res.*
import android.support.annotation.*
import android.text.*
import android.util.*
import android.view.*
import android.view.inputmethod.*
import android.widget.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*
import kotlinx.coroutines.experimental.channels.*


/**
 * Unsafely sets the onclick listener
 * possibility of dual calling.
 * @receiver View the view to set the on click listener for
 * @param listener EmptyFunction the function to call when getting clicked
 */
@UiThread
inline fun View.setOnClick(crossinline listener: EmptyFunction) {
    setOnClickListener { listener() }
}

/**
 * measures this views size and calls the given method with the result.
 *
 * @receiver View the view to measure
 * @param afterMeasureAction (with: Int, height: Int) -> Unit the callback getting called once the view have been layed out
 */
@UiThread
inline fun View.measureSize(crossinline afterMeasureAction: (with: Int, height: Int) -> Unit) {
    if (!viewTreeObserver.isAlive) {
        return
    }
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            afterMeasureAction(width, height)
        }
    })
}


@UiThread
fun View.setOnclickAsyncSuspend(action: AsyncFunctionUnit<Context>) {
    val eventActor = GlobalScope.actor<Unit>(Dispatchers.Main, capacity = Channel.CONFLATED) {
        channel.consumeEach {
            val cont = context ?: return@actor
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
    val eventActor = GlobalScope.actor<Unit>(Dispatchers.Main, capacity = Channel.CONFLATED) {
        channel.consumeEach {
            val cont = context ?: return@actor
            tryAndLog("onclickAsync") {
                action(cont)
            }
        }
    }
    setOnClick { eventActor.offer(Unit) }
}

@UiThread
fun View.setOnclickAsyncEmpty(action: EmptyFunction) {
    this.setOnclickAsync { _ -> action() }
}

@UiThread
fun View.getTypedArrayFor(attributeSet: AttributeSet,
                          @StyleableRes style: IntArray,
                          defStyleAttr: Int = 0,
                          defStyleRes: Int = 0): TypedArray =
        context.getTypedArrayFor(attributeSet, style, defStyleAttr, defStyleRes)


/**
 * resets all transformations on a view (x, y, z) to 0f.
 * @receiver View
 */
@UiThread
fun View.resetTransformations() {
    translationX = 0f
    if (isApiOverOrEqualTo(21)) {
        translationZ = 0f
    }
    translationY = 0f
}


/**
 * Toggles between visible and gone.
 * so if this view is visible then after calling it will be gone
 * and vice versa
 */
@UiThread
fun View.toggleVisibilityGone() {
    isVisible.ifTrue(this::gone).ifFalse(this::visible)
}

/**
 * Returns true iff its visible, false otherwise.
 */
val View.isVisible: Boolean
    @UiThread
    get() = visibility == View.VISIBLE

/**
 * returns true iff its gone otherwise false
 */
val View.isGone: Boolean
    @UiThread
    get() = visibility == View.GONE

/**
 * returns true iff its invisible, otherwise false
 */
val View.isInvisible: Boolean
    @UiThread
    get() = visibility == View.INVISIBLE

/**
 * makes the view completely gone.
 * @receiver View
 */
@UiThread
fun View.gone() {
    this.visibility = View.GONE
}

/**
 * makes the view visible
 * @receiver View
 */
@UiThread
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**
 * makes the view invisible in android terms
 * @receiver View
 */
@UiThread
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * If true, the view is made visible, if false its made gone
 * @receiver View
 * @param condition Boolean if true the view is made visible, gone otherwise
 */
@UiThread
fun View.visibleOrGone(condition: Boolean) {
    if (condition) {
        visible()
    } else {
        gone()
    }
}

/**
 * Makes the supplied array of (optional) views gone.
 * @receiver Array<View?>
 */
@UiThread
fun Array<View?>.goneViews() = ViewHelper.goneViews(*this)


/**
 * Makes the supplied list of (optional) views gone
 * @receiver List<View?>
 */
@UiThread
fun List<View?>.goneViews() = ViewHelper.goneViews(this)

/**
 * Makes the supplied list of (optional) views visible
 * @receiver List<View?>
 */
@UiThread
fun List<View?>.visibleViews() = ViewHelper.showViews(this)

@UiThread
object ViewHelper {
    /**
     * Marks all the given views as gone.
     * @param views Array<out View?>
     */
    fun goneViews(vararg views: View?) {
        views.forEach { it?.gone() }
    }

    /**
     * Marks all of the given views as gone
     * @param views Iterable<View?>
     */
    fun goneViews(views: Iterable<View?>) {
        views.forEachNotNull(View::gone)
    }

    /**
     * Shows and gone's the given views
     * @param toShow View? the view to show
     * @param toGone View? the view to gone
     */
    fun showGoneView(toShow: View?, toGone: View?) {
        toShow?.visible()
        toGone?.gone()
    }

    /**
     * Shows the given views.
     * @param views Iterable<View?>
     */
    fun showViews(views: Iterable<View?>) {
        views.forEachNotNull(View::visible)
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

/**
 * disables this view (isEnabled = false, isClickable = false)
 * if it is a ViewGroup, then all children will be disabled as well
 * @receiver View
 */
@UiThread
fun View.disable() {
    isEnabled = false
    isClickable = false
    (this as? ViewGroup)?.children?.forEach(View::disable)
}


/**
 * Enables this view (isEnabled = true, isClickable = true)
 * if it is a ViewGroup, then all children will be enabled as well
 * @receiver View
 */
@UiThread
fun View.enable() {
    isEnabled = true
    isClickable = true
    (this as? ViewGroup)?.children?.forEach(View::enable)
}

/**
 * Marks an EditText to be the "last one" akk the one with the ime done action button on the keyboard.
 * @receiver EditText
 */
@UiThread
fun EditText.imeDone() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    inputType = inputType xor InputType.TYPE_TEXT_FLAG_MULTI_LINE xor InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
}