package com.commonsense.android.kotlin.views.widgets

import android.content.*
import android.graphics.*
import android.util.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.extensions.*
import com.commonsense.android.kotlin.views.widgets.base.*

/**
 * Created by Kasper Tvede
 */
class DotView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseCustomDrawView(context, attrs, defStyleAttr) {

    private val fillSpace = -1F

    override fun getStyleResource(): IntArray? = R.styleable.DotView

    private val paint by lazy { Paint() }

    //<editor-fold desc="Radius">
    private val innerRadius by lazy { dimensionVariable(fillSpace, R.styleable.DotView_radius) }

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
        if (radius.equals(fillSpace, 0.1f)) {
            val expectedWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            val size = minOf(expectedHeight, expectedWidth)
            setMeasuredDimension(size, size)
        } else {
            val radiusX2 = radius.toInt() * 2
            setMeasuredDimension(radiusX2, expectedHeight)
        }
    }


    override fun doDrawing(canvas: Canvas) {
        val size = if (radius.equals(fillSpace, 0.1f)) {
            minOf(canvas.height, canvas.width) / 2f
        } else {
            radius
        }
        canvas.drawCircle(canvas.centerX, canvas.centerY, size, paint)
    }


}