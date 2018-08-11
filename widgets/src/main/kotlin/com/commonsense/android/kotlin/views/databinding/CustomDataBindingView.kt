package com.commonsense.android.kotlin.views.databinding

import android.content.*
import android.content.res.*
import android.databinding.*
import android.util.*
import android.view.*
import android.widget.*
import com.commonsense.android.kotlin.views.datastructures.*
import com.commonsense.android.kotlin.views.widgets.*
import java.lang.ref.*

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

    val binding: T by lazy {
        val inflaterFunction = inflate()
        inflaterFunction(LayoutInflater.from(context), this@CustomDataBindingView, true)
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