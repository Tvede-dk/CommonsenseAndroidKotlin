package com.commonsense.android.kotlin.views.widgets.base

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import com.commonsense.android.kotlin.views.datastructures.ViewVariable
import com.commonsense.android.kotlin.views.extensions.contentSize
import com.commonsense.android.kotlin.views.widgets.LateAttributes
import java.lang.ref.WeakReference

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
abstract class BaseCustomViewGroup : ViewGroup, LateAttributes {

    constructor(context: Context) : super(context)


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupTypedArray(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupTypedArray(attrs, defStyleAttr)
    }

    private val contentRect: Rect = Rect()

    override val attributes: MutableList<WeakReference<ViewVariable<*>>> = mutableListOf()

    override var partialTypedArray: TypedArray? = null

    /**
     * if true, we will perform our own background drawing;
     * if false, use the default
     */
    private var _willDrawOwnBackground = false

    var willDrawOwnBackground
        get() = _willDrawOwnBackground
        set(value) {
            //skip equal value
            if (value == willDrawOwnBackground) {
                return
            }
            //it changed
            _willDrawOwnBackground = value
            //move bitmap according.
            if (value) {
                storedBackground = background
                super.setBackground(null)
            } else {
                background = storedBackground
                storedBackground = null
            }
        }

    var storedBackground: Drawable? = null

    fun getContentSize(): Rect {
        contentSize(contentRect)
        return contentRect
    }

    override fun afterSetupView() {
        // what use would this have now ? ??
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        afterFinishInflate()
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        if (willDrawOwnBackground) {
            //hijack the drawable. setBackground setups a lot of things.
            storedBackground = background
            super.setBackground(null) //remove it such that the underlying view does not magically use it.
        }
    }


    override fun onDraw(canvas: Canvas?) {
        if (canvas != null && willDrawOwnBackground) {
            storedBackground?.setBounds(0, 0, width, height)
            drawBackground(canvas, storedBackground)
        }
        super.onDraw(canvas)
    }

    open fun drawBackground(canvas: Canvas, background: Drawable?) {

    }


}