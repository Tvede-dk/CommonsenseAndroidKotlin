package com.commonsense.android.kotlin.android.widgets.base

import android.content.Context
import android.databinding.ViewDataBinding
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by Kasper Tvede on 13-06-2017.
 * Made for custom "controls" (collections of view(s))
 *
 */
abstract class CustomDataBindingView<T : ViewDataBinding> : FrameLayout {

    val binding: T  by lazy {
        val inflaterFunction = inflate()
        inflaterFunction(LayoutInflater.from(context), this, true)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    abstract fun inflate(): (inflater: LayoutInflater, parent: ViewGroup, attach: Boolean) -> T


}