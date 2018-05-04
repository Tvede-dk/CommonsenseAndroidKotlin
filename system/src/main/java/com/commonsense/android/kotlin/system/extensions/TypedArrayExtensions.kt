package com.commonsense.android.kotlin.system.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StyleableRes
import android.support.v7.content.res.AppCompatResources

/**
 * Created by kasper on 29/05/2017.
 */


fun TypedArray.getDrawableSafe(@StyleableRes style: Int, context: Context): Drawable? {
    if (isApiEqualOrGreater(21)) {
        return ifHaveOrNull(style, { getDrawable(style) })
    }

    return ifHaveOrNull(style) {
        val defValue = -1
        @DrawableRes
        val drawableRes = getResourceId(style, defValue)
        if (drawableRes != defValue) {
            return@ifHaveOrNull AppCompatResources.getDrawable(context, drawableRes)
        }
        return@ifHaveOrNull null
    }
}

fun TypedArray.getTextSafe(@StyleableRes style: Int): CharSequence? {
    return ifHaveOrNull(style) {
        return@ifHaveOrNull getText(style)
    }
}

fun TypedArray.getTextSafe(@StyleableRes style: Int, defaultValue: CharSequence?): CharSequence? {
    return getTextSafe(style) ?: defaultValue
}

fun TypedArray.getColorSafe(@StyleableRes style: Int): Int? {
    return ifHaveOrNull(style) {
        getColorStateList(style)?.defaultColor
    }
}

@ColorInt
fun TypedArray.getColorSafe(@StyleableRes style: Int, @ColorInt defaultColor: Int): Int {
    return getColorSafe(style) ?: defaultColor
}

inline fun <T> TypedArray.ifHaveOrNull(@StyleableRes style: Int, crossinline actionIf: () -> T?): T? {
    if (hasValue(style)) {
        return actionIf()
    }
    return null
}






