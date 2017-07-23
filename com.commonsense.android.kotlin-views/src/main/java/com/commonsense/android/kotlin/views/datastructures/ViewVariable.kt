package com.commonsense.android.kotlin.views.datastructures

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.StyleableRes
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.system.extensions.getDrawableSafe
import com.commonsense.android.kotlin.system.extensions.getTextSafe
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

    open fun setNoUpdate(newValue: T) {
        innerValue.setWithNoUpdate(newValue)
    }

    open fun parse(typedArray: TypedArray, context: Context) {
        parseFrom(typedArray, context)?.let(this::setNoUpdate)
    }

    protected abstract fun parseFrom(typedArray: TypedArray, context: Context): T?

    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T = innerValue.value

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        innerValue.value = value
    }


    open val value
        get() = innerValue.value

}

open class TextViewVariable(styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<CharSequence?>(null, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): CharSequence? =
            typedArray.getTextSafe(styleIndex)
}

open class ColorValueViewVariable(defaultValue: Int, styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Int>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Int? =
            typedArray.getColor(styleIndex, 0)
}

open class BooleanViewVariable(defaultValue: Boolean, styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Boolean>(defaultValue, styleIndex, toAttachTo, onUpdate) {

    override fun parseFrom(typedArray: TypedArray, context: Context): Boolean? =
            typedArray.getBoolean(styleIndex, false)
}

open class DrawableViewVariable(styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Drawable?>(null, styleIndex, toAttachTo, onUpdate) {

    override fun parseFrom(typedArray: TypedArray, context: Context): Drawable? =
            typedArray.getDrawableSafe(styleIndex, context)

}


class BooleanCallbackViewVariable(defaultValue: Boolean, styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : BooleanViewVariable(defaultValue, styleIndex, toAttachTo, onUpdate) {

    var onChanged: EmptyFunction? = null


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        val before = value
        super.setValue(thisRef, property, value)
        if (before != value) {
            onChanged?.invoke()
        }
    }
}