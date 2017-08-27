package com.commonsense.android.kotlin.views.widgets.base

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.views.datastructures.ViewVariable
import com.commonsense.android.kotlin.views.widgets.LateAttributes
import java.lang.ref.WeakReference

/**
 * Created by kasper on 21/08/2017.
 */

abstract class BaseCustomDrawView : View, LateAttributes {

    override val attributes = mutableListOf<WeakReference<ViewVariable<*>>>()

    override var partialTypedArray: TypedArray? = null

    var shouldDrawSuper = true

    var willDrawOwnBackground = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupTypedArray(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupTypedArray(attrs, defStyleAttr)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        afterFinishInflate()
    }

    init {
        //we will do drawing.
        setWillNotDraw(false)
    }

    override fun afterSetupView() {
        setupBeforeDraw()
    }

    override fun updateView() {
        setupBeforeDraw()
        postInvalidate()
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        if (willDrawOwnBackground) {
            //hijack the drawable. setBackground setups a lot of things.
            storedBackground = background
            super.setBackground(null) //remove it such that the underlying view does not magically use it.
        }
    }

    private var storedBackground: Drawable? = null

    override fun draw(canvas: Canvas?) {
        if (canvas == null) {
            super.draw(canvas)
            return
        }
        if (willDrawOwnBackground) {
            drawBackground(canvas, storedBackground)
        }
        //if we will not draw the background, then super should be called always.
        (shouldDrawSuper || !willDrawOwnBackground).ifTrue { super.draw(canvas) }
        doDrawing(canvas)
    }

    open fun drawBackground(canvas: Canvas, background: Drawable?) {
        background?.draw(canvas)
    }

    abstract fun doDrawing(canvas: Canvas)

    /**
     * Setup required classes / allocations before the onDraw method.
     */
    abstract fun setupBeforeDraw()

}
