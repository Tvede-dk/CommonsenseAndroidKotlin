package com.commonsense.android.kotlin.views.databinding

import android.content.Context
import android.content.res.TypedArray
import android.databinding.ViewDataBinding
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.commonsense.android.kotlin.views.datastructures.ViewVariable
import com.commonsense.android.kotlin.views.widgets.LateAttributes
import java.lang.ref.WeakReference

/**
 * Created by Kasper Tvede on 13-06-2017.
 * Made for custom "controls" (collections of view(s))
 *
 */
typealias InflaterFunction<Vm> = (inflater: LayoutInflater, parent: ViewGroup, attach: Boolean) -> Vm

abstract class CustomDataBindingView<T : ViewDataBinding> : FrameLayout, LateAttributes {

    override var partialTypedArray: TypedArray? = null

    override val attributes = mutableListOf<WeakReference<ViewVariable<*>>>()

    override fun onFinishInflate() {
        super.onFinishInflate()
        afterFinishInflate()
    }

    val binding: T  by lazy {
        val inflaterFunction = inflate()
        inflaterFunction(LayoutInflater.from(context), this, true)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupTypedArray(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupTypedArray(attrs, defStyleAttr)
    }

    abstract fun inflate(): InflaterFunction<T>

}