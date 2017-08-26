package com.commonsense.android.kotlin.views.widgets.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.views.datastructures.ViewVariable
import com.commonsense.android.kotlin.views.widgets.ViewAttribute
import java.lang.ref.WeakReference

/**
 * Created by kasper on 21/08/2017.
 */

abstract class BaseCustomDrawView : View, ViewAttribute {

    override val attributes = mutableListOf<WeakReference<ViewVariable<*>>>()


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        //we will do drawing.
        setWillNotDraw(false)
    }

    override fun afterSetupView() {
        setupBeforeDraw()
    }

    override fun updateView() {
        setupBeforeDraw()
        invalidate()
    }

    /**
     * Setup required classes / allocations before the onDraw method.
     */
    abstract fun setupBeforeDraw()

}
