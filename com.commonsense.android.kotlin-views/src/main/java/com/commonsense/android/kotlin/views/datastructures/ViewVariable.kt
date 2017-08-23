package com.commonsense.android.kotlin.views.datastructures

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.*
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

    fun getInnerValue(): T = innerValue.value

}

open class TextViewVariable(@StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<CharSequence?>(null, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): CharSequence? =
            typedArray.getTextSafe(styleIndex)
}

open class ColorValueViewVariable(@ColorInt defaultValue: Int, @StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Int>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Int? =
            typedArray.getColor(styleIndex, getInnerValue())
}

open class BooleanViewVariable(defaultValue: Boolean, @StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Boolean>(defaultValue, styleIndex, toAttachTo, onUpdate) {

    override fun parseFrom(typedArray: TypedArray, context: Context): Boolean? =
            typedArray.getBoolean(styleIndex, getInnerValue())
}

open class DrawableViewVariable(@StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Drawable?>(null, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Drawable? =
            typedArray.getDrawableSafe(styleIndex, context)
}

open class IntViewVariable(defaultValue: Int, @StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<Int>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Int? =
            typedArray.getInteger(styleIndex, getInnerValue())

}

class DimensionViewVariable(@Dimension defaultValue: Float, @StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : ViewVariable<@android.support.annotation.Dimension Float>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Float? =
            typedArray.getDimension(styleIndex, getInnerValue())
}

class BooleanCallbackViewVariable(defaultValue: Boolean, @StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction)
    : BooleanViewVariable(defaultValue, styleIndex, toAttachTo, onUpdate) {

    var onChanged: EmptyFunction? = null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        val before = this.getInnerValue()
        super.setValue(thisRef, property, value)
        if (before != value) {
            onChanged?.invoke()
        }
    }
}

class DrawableCallbackViewVariable(@StyleableRes styleIndex: Int, toAttachTo: ViewAttributeList, onUpdate: EmptyFunction) :
        DrawableViewVariable(styleIndex, toAttachTo, onUpdate) {

    var onChanged: EmptyFunction? = null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Drawable?) {
        val before = this.getInnerValue()
        super.setValue(thisRef, property, value)
        if (before != value) {
            onChanged?.invoke()
        }
    }
}