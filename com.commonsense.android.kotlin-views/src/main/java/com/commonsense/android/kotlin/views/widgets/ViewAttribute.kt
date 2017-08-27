package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.StyleableRes
import android.support.annotation.VisibleForTesting
import android.util.AttributeSet
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.use
import com.commonsense.android.kotlin.system.extensions.getTypedArrayFor
import com.commonsense.android.kotlin.system.logging.tryAndLog
import com.commonsense.android.kotlin.views.datastructures.*
import java.lang.ref.WeakReference

/**
 * Created by Kasper Tvede on 13-06-2017.
 */

interface ViewAttribute {
    /**
     * if any custom attributes, return the name of the styles.
     */
    @StyleableRes
    fun getStyleResource(): IntArray?

    /**
     * if any custom attributes, parse them
     */
    fun parseTypedArray(data: TypedArray) {
        val context = getContext()
        attributes.forEach { it.use { parse(data, context) } }
    }

    /**
     * callback for when to update the ui (from state).
     */
    fun updateView()

    fun afterSetupView()

    fun getContext(): Context

    fun post(action: Runnable): Boolean

    fun post(action: EmptyFunction) {
        post(Runnable(action))
    }

    fun invalidate()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val attributes: MutableList<WeakReference<ViewVariable<*>>>
}


fun ViewAttribute.ColorVariable(@ColorInt defaultValue: Int, @StyleableRes styleIndex: Int): ColorValueViewVariable =
        ColorValueViewVariable(defaultValue, styleIndex, attributes, this::updateView)

fun ViewAttribute.ColorVariable(@StyleableRes styleIndex: Int): ColorValueViewVariable =
        ColorVariable(Color.BLACK, styleIndex)

fun ViewAttribute.IntVariable(defaultValue: Int, @StyleableRes styleIndex: Int): IntViewVariable =
        IntViewVariable(defaultValue, styleIndex, attributes, this::updateView)

fun ViewAttribute.IntVariable(@StyleableRes styleIndex: Int): IntViewVariable =
        IntVariable(0, styleIndex)

fun ViewAttribute.TextVariable(@StyleableRes styleIndex: Int): TextViewVariable =
        TextViewVariable(styleIndex, attributes, this::updateView)

fun ViewAttribute.BooleanVariable(defaultValue: Boolean, @StyleableRes styleIndex: Int): BooleanViewVariable =
        BooleanViewVariable(defaultValue, styleIndex, attributes, this::updateView)

fun ViewAttribute.BooleanVariable(@StyleableRes styleIndex: Int): BooleanViewVariable =
        BooleanVariable(false, styleIndex)


fun ViewAttribute.DrawableVariable(@StyleableRes styleIndex: Int): DrawableViewVariable =
        DrawableViewVariable(styleIndex, attributes, this::updateView)


fun ViewAttribute.DimensionVariable(@StyleableRes styleIndex: Int): DimensionViewVariable =
        DimensionVariable(0f, styleIndex)

fun ViewAttribute.DimensionVariable(@Dimension defaultValue: Float, @StyleableRes styleIndex: Int)
        : DimensionViewVariable =
        DimensionViewVariable(defaultValue, styleIndex, attributes, this::updateView)


fun ViewAttribute.prepareAttributes(attrs: AttributeSet? = null, defStyleAttr: Int? = null) {
    val style = getStyleResource()
    if (style != null && attrs != null) {
        val typedArray = getContext().getTypedArrayFor(attrs, style, defStyleAttr ?: 0)
        tryAndLog(this::class) {
            parseTypedArray(typedArray)
        }
        typedArray.recycle()
        afterSetupView()
        updateView()

    } else {
        afterSetupView()
    }
}


interface LateAttributes : ViewAttribute {
    var partialTypedArray: TypedArray?

    fun setupTypedArray(attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val style = getStyleResource()
        if (style != null && attrs != null)
            partialTypedArray = getContext().getTypedArrayFor(attrs, style, defStyleAttr)
    }

    fun afterFinishInflate() {
        partialTypedArray?.let {
            tryAndLog(this::class) {
                parseTypedArray(it)
            }
            it.recycle()
        }
        partialTypedArray = null
        afterSetupView()
        updateView()
    }
}

