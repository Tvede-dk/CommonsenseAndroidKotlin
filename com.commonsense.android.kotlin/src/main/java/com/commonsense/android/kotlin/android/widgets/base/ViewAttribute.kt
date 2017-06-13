package com.commonsense.android.kotlin.android.widgets.base

import android.content.res.TypedArray
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.android.extensions.widets.getTypedArrayFor
import com.commonsense.android.kotlin.android.logging.L

/**
 * Created by Kasper Tvede on 13-06-2017.
 */

interface ViewAttribute {
    /**
     * if any custom attributes, return the name of the styles.
     */
    @StyleableRes
    abstract fun getStyleResource(): IntArray?

    /**
     * if any custom attributes, parse them
     */
    abstract fun parseTypedArray(data: TypedArray)

    /**
     * callback for when to update the ui (from state).
     */
    abstract fun updateView()

    abstract fun afterSetupView()
}


fun ViewAttribute.prepareAttributes(view: View, attrs: AttributeSet? = null, defStyleAttr: Int? = null) {
    val style = getStyleResource()
    if (style != null && attrs != null) {
        val typedArray = view.getTypedArrayFor(attrs, style, defStyleAttr ?: 0)
        try {
            parseTypedArray(typedArray)
            afterSetupView()
            updateView()
        } catch (e: Exception) {
            L.warning(this::class.java.simpleName, "failed to parse typed array, ", e)
        } finally {
            typedArray.recycle()
        }
    } else {
        afterSetupView()
    }
}