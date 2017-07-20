package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.content.res.TypedArray
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import com.commonsense.android.kotlin.system.extensions.getTypedArrayFor
import com.commonsense.android.kotlin.system.logging.L

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
    fun parseTypedArray(data: TypedArray)

    /**
     * callback for when to update the ui (from state).
     */
    fun updateView()

    fun afterSetupView()

    fun getContext(): Context
}


fun ViewAttribute.prepareAttributes(attrs: AttributeSet? = null, defStyleAttr: Int? = null) {
    val style = getStyleResource()
    if (style != null && attrs != null) {
        val typedArray = getContext().getTypedArrayFor(attrs, style, defStyleAttr ?: 0)
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
