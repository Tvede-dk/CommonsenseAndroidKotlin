package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.commonsense.android.kotlin.base.extensions.equals
import com.commonsense.android.kotlin.views.R
import com.commonsense.android.kotlin.views.extensions.centerX
import com.commonsense.android.kotlin.views.extensions.centerY
import com.commonsense.android.kotlin.views.widgets.base.BaseCustomDrawView

/**
 * Created by Kasper Tvede on 26-08-2017.
 */
class DotView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseCustomDrawView(context, attrs, defStyleAttr) {

    val Fill_space = -1F

    override fun getStyleResource(): IntArray? = R.styleable.DotView

    private val paint by lazy { Paint() }

    //<editor-fold desc="Radius">
    private val innerRadius by lazy { dimensionVariable(Fill_space, R.styleable.DotView_radius) }

    var radius: Float by innerRadius
    //</editor-fold>

    //<editor-fold desc="Color">
    private val innerColor by lazy { colorVariable(Color.BLACK, R.styleable.DotView_color) }

    var color: Int by innerColor
    //</editor-fold>

    override fun setupBeforeDraw() {
        paint.color = color
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expectedHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        if (radius.equals(Fill_space, 0.1f)) {
            val expectedWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            val size = minOf(expectedHeight, expectedWidth)
            setMeasuredDimension(size, size)
        } else {
            val radiusX2 = radius.toInt() * 2
            setMeasuredDimension(radiusX2, expectedHeight)
        }
    }


    override fun doDrawing(canvas: Canvas) {
        val size = if (radius.equals(Fill_space, 0.1f)) {
            minOf(canvas.height, canvas.width) / 2f
        } else {
            radius
        }
        canvas.drawCircle(canvas.centerX, canvas.centerY, size, paint)
    }


}