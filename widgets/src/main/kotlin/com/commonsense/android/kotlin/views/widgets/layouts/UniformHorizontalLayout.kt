@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.widgets.layouts

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.util.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.views.extensions.*

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
class UniformHorizontalLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : UniformBaseLayout(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val contentRect = getContentSize()
        val widthOfChild = childSizeOf(contentWidth)
        var currentLeft = contentRect.left + contentMarginInt
        val bottomOffset = contentRect.bottom - contentMarginInt
        val topOffset = contentRect.top + contentMarginInt

        visibleChildren.forEach {
            it.layout(currentLeft,
                    topOffset,
                    currentLeft + widthOfChild,
                    bottomOffset)
            currentLeft += widthOfChild + contentMarginInt
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val widthOfChild = childSizeOf(contentWidth)

        var maxHeight = suggestedMinimumHeight
        val ourMeasureWidth = MeasureSpec.makeMeasureSpec(widthOfChild, MeasureSpec.EXACTLY)

        visibleChildren.forEach {
            measureChild(it, ourMeasureWidth, heightMeasureSpec)
            maxHeight = maxOf(maxHeight, it.measuredHeight)
        }
        setMeasuredDimension(width, maxHeight)
    }


    override fun doDrawOurOwnBackground(canvas: Canvas, background: Drawable) {
        val bottomOffset = canvas.height - contentMarginInt
        //top border and bottom border
        canvas.draw(background, 0, 0, canvas.width, contentMarginInt)
        canvas.draw(background, 0, bottomOffset,
                canvas.width, canvas.height)
        //all things "in between" including the sides
        val widthOfChild = childSizeOf(contentWidth)
        var currentLeft = 0
        (visibleChildrenCount + 1).forEach {
            canvas.draw(background,
                    currentLeft,
                    contentMarginInt,
                    currentLeft + contentMarginInt,
                    bottomOffset)
            currentLeft += widthOfChild + contentMarginInt
        }
    }

}
