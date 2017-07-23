package com.commonsense.android.kotlin.views.datastructures

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.StyleableRes
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.system.extensions.getDrawableSafe
import com.commonsense.android.kotlin.system.extensions.getTextSafe
import com.commonsense.android.kotlin.views.R
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Created by Kasper Tvede on 22-07-2017.
 */

typealias ViewAttributeList = MutableList<WeakReference<ViewVariable<*>>>

abstract class ViewVariable<T>(initialValue: T, @StyleableRes val styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction) {

    private val innerValue: UpdateVariable<T> = UpdateVariable(initialValue, onUpdate)

    init {
        toAttachTo.add(this.weakReference())
    }

    fun setNoUpdate(newValue: T) {
        innerValue.setWithNoUpdate(newValue)
    }

    fun parse(typedArray: TypedArray, context: Context) {
        parseFrom(typedArray, context)?.let(this::setNoUpdate)
    }

    protected abstract fun parseFrom(typedArray: TypedArray, context: Context): T?

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = innerValue.value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        innerValue.value = value
    }

}

class CharSequenceViewVariable(styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<CharSequence?>(null, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): CharSequence? =
            typedArray.getTextSafe(styleIndex)
}

class ColorValueViewVariable(defaultValue: Int, styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Int>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Int? =
            typedArray.getColor(styleIndex, 0)
}

class BooleanViewVariable(defaultValue: Boolean, styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Boolean>(defaultValue, styleIndex, toAttachTo, onUpdate) {

    override fun parseFrom(typedArray: TypedArray, context: Context): Boolean? =
            typedArray.getBoolean(styleIndex, false)
}

class DrawableViewVariable(styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Drawable?>(null, styleIndex, toAttachTo, onUpdate) {

    override fun parseFrom(typedArray: TypedArray, context: Context): Drawable? =
            typedArray.getDrawableSafe(styleIndex, context)

}