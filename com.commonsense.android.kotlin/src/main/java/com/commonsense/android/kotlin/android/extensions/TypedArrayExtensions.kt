package com.commonsense.android.kotlin.android.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.support.v7.content.res.AppCompatResources

/**
 * Created by kasper on 29/05/2017.
 */


fun TypedArray.getDrawableSafe(@StyleRes style: Int, context: Context): Drawable? {
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

fun TypedArray.getTextSafe(@StyleRes style: Int): CharSequence? {
    return ifHaveOrNull(style) {
        return@ifHaveOrNull getText(style)
    }
}


inline fun <T> TypedArray.ifHaveOrNull(@StyleRes style: Int, crossinline actionIf: () -> T?): T? {
    if (hasValue(style)) {
        return actionIf()
    }
    return null
}
