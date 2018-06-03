package com.commonsense.android.kotlin.views.widgets.layouts

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.commonsense.android.kotlin.base.extensions.forEach
import com.commonsense.android.kotlin.views.extensions.contentHeight
import com.commonsense.android.kotlin.views.extensions.draw
import com.commonsense.android.kotlin.views.extensions.visibleChildren
import com.commonsense.android.kotlin.views.extensions.visibleChildrenCount

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
class UniformVerticalLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : UniformBaseLayout(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val contentRect = getContentSize()
        val heightOfChild = childSizeOf(contentHeight)

        var currentTop = contentRect.top + contentMarginInt
        val leftOffset = contentRect.left + contentMarginInt
        val rightOffset = contentRect.right - contentMarginInt

        visibleChildren.forEach {
            it.layout(leftOffset,
                    currentTop,
                    rightOffset,
                    currentTop + heightOfChild)
            currentTop += heightOfChild + contentMarginInt
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val heightOfChild = childSizeOf(contentHeight)

        var maxWidth = suggestedMinimumWidth
        val ourMeasureHeight = MeasureSpec.makeMeasureSpec(heightOfChild, MeasureSpec.EXACTLY)

        visibleChildren.forEach {
            measureChild(it, widthMeasureSpec, ourMeasureHeight)
            maxWidth = maxOf(maxWidth, it.measuredWidth)
        }
        setMeasuredDimension(maxWidth, height)
    }


    override fun doDrawOurOwnBackground(canvas: Canvas, background: Drawable) {

        val leftOffset = contentMarginInt
        val rightOffset = canvas.width - contentMarginInt
        //left and right border
        canvas.draw(background, 0, 0, contentMarginInt, canvas.height)
        canvas.draw(background, canvas.width - contentMarginInt, 0,
                canvas.width, canvas.height)
        //all things "in between" including the sides
        val heightOfChild = childSizeOf(contentHeight)
        var currentTop = 0
        (visibleChildrenCount + 1).forEach {
            canvas.draw(background,
                    leftOffset,
                    currentTop,
                    rightOffset,
                    currentTop + contentMarginInt)
            currentTop += heightOfChild + contentMarginInt
        }
    }
}