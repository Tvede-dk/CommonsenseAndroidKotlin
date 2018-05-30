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

@Suppress("NOTHING_TO_INLINE")
inline fun TypedArray.getDrawableSafe(@StyleableRes style: Int, context: Context): Drawable? {
    if (isApiOverOrEqualTo(21)) {
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

@Suppress("NOTHING_TO_INLINE")
inline fun TypedArray.getTextSafe(@StyleableRes style: Int): CharSequence? {
    return ifHaveOrNull(style) {
        return@ifHaveOrNull getText(style)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun TypedArray.getTextSafe(@StyleableRes style: Int,
                                  defaultValue: CharSequence?): CharSequence? {
    return getTextSafe(style) ?: defaultValue
}

@Suppress("NOTHING_TO_INLINE")
@ColorInt
inline fun TypedArray.getColorSafe(@StyleableRes style: Int): Int? {
    return ifHaveOrNull(style) {
        getColorStateList(style)?.defaultColor
    }
}

@Suppress("NOTHING_TO_INLINE")
@ColorInt
inline fun TypedArray.getColorSafe(@StyleableRes style: Int,
                                   @ColorInt defaultColor: Int): Int {
    return getColorSafe(style) ?: defaultColor
}

inline fun <T> TypedArray.ifHaveOrNull(@StyleableRes style: Int,
                                       crossinline actionIf: () -> T?): T? {
    if (hasValue(style)) {
        return actionIf()
    }
    return null
}






