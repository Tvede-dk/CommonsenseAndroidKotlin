package com.commonsense.android.kotlin.views.datastructures

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.StyleableRes
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.system.extensions.getColorSafe
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

    var onChanged: EmptyFunction? = null

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        val beforeValue = innerValue
        innerValue.value = newValue
        if (beforeValue != newValue) {
            onChanged?.invoke()
        }
    }

    fun getInnerValue(): T = innerValue.value

}

open class TextViewVariable(@StyleableRes styleIndex: Int,
                            toAttachTo: ViewAttributeList,
                            onUpdate: EmptyFunction)
    : AbstractViewVariable<CharSequence?>(null, TypedArray::getTextSafe, styleIndex, toAttachTo, onUpdate)

open class ColorValueViewVariable(@ColorInt defaultValue: Int,
                                  @StyleableRes styleIndex: Int,
                                  toAttachTo: ViewAttributeList,
                                  onUpdate: EmptyFunction)
    : AbstractViewVariable<@ColorInt Int>(defaultValue, TypedArray::getColorSafe, styleIndex, toAttachTo, onUpdate)

open class BooleanViewVariable(defaultValue: Boolean,
                               @StyleableRes styleIndex: Int,
                               toAttachTo: ViewAttributeList,
                               onUpdate: EmptyFunction)
    : AbstractViewVariable<Boolean>(defaultValue, TypedArray::getBoolean, styleIndex, toAttachTo, onUpdate)

open class DrawableViewVariable(@StyleableRes styleIndex: Int,
                                toAttachTo: ViewAttributeList,
                                onUpdate: EmptyFunction)
    : ViewVariable<Drawable?>(null, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): Drawable? =
            typedArray.getDrawableSafe(styleIndex, context)
}

open class FloatViewVariable(defaultValue: Float,
                             @StyleableRes styleIndex: Int,
                             toAttachTo: ViewAttributeList,
                             onUpdate: EmptyFunction)
    : AbstractViewVariable<Float>(defaultValue, TypedArray::getFloat, styleIndex, toAttachTo, onUpdate)

open class IntViewVariable(defaultValue: Int,
                           @StyleableRes styleIndex: Int,
                           toAttachTo: ViewAttributeList,
                           onUpdate: EmptyFunction)
    : AbstractViewVariable<Int>(defaultValue, TypedArray::getInt, styleIndex, toAttachTo, onUpdate)


open class DimensionViewVariable(@Dimension defaultValue: Float,
                                 @StyleableRes styleIndex: Int,
                                 toAttachTo: ViewAttributeList,
                                 onUpdate: EmptyFunction)
    : AbstractViewVariable<@Dimension Float>(defaultValue, TypedArray::getDimension, styleIndex, toAttachTo, onUpdate)


abstract class AbstractViewVariable<T>(defaultValue: T,
                                       private val extractor: (TypedArray, Int, T) -> T?,
                                       @StyleableRes styleIndex: Int,
                                       toAttachTo: ViewAttributeList,
                                       onUpdate: EmptyFunction)

    : ViewVariable<T>(defaultValue, styleIndex, toAttachTo, onUpdate) {
    override fun parseFrom(typedArray: TypedArray, context: Context): T? =
                extractor(typedArray, styleIndex, getInnerValue())
}