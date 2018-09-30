@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate", "LeakingThis")

package com.commonsense.android.kotlin.views.widgets.layouts

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.util.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.widgets.*
import com.commonsense.android.kotlin.views.widgets.base.*

/**
 * Created by Kasper Tvede on 27-08-2017.
 */

abstract class UniformBaseLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseCustomViewGroup(context, attrs, defStyleAttr) {

    init {
        setWillNotDraw(false)
        willDrawOwnBackground = true
    }

    //<editor-fold desc="view update and resources">
    override fun getStyleResource(): IntArray? = R.styleable.UniformBaseLayout

    override fun updateView() {
        invalidate()
        requestLayout()
    }
    //</editor-fold>

    //<editor-fold desc="Force draw background">
    private val innerForceDrawBackground by lazy {
        booleanVariable(false, R.styleable.UniformBaseLayout_forceDrawBackground)
    }
    var forceDrawBackground by innerForceDrawBackground
    //</editor-fold>

    //<editor-fold desc="Content margin">
    private val innerContentMargin by lazy { dimensionVariable(0f, R.styleable.UniformBaseLayout_contentMargin) }

    var contentMargin: Float by innerContentMargin
    //</editor-fold>

    /**
     * The total amount of margin across the whole view.
     */
    protected inline val totalMargin: Float
        get() = (childCount + 1) * contentMargin

    protected inline val contentMarginInt: Int
        get() = contentMargin.toInt()

    /**
     * Calculates the size for each children based on the total size (by subtracting the total margin).
     */
    fun childSizeOf(totalSize: Int): Int {
        return ((totalSize - totalMargin) / maxOf(childCount, 1)).toInt()
    }

    val haveContentMargin: Boolean
        get() = !contentMargin.equals(0f, 0.1f)

    override fun drawBackground(canvas: Canvas, background: Drawable?) {
        super.drawBackground(canvas, background)
        //force drawing
        if (forceDrawBackground || background == null) {
            background?.draw(canvas)
            return
        }
        //no background / borders
        if (!haveContentMargin) {
            return
        }
        doDrawOurOwnBackground(canvas, background)
    }

    abstract fun doDrawOurOwnBackground(canvas: Canvas, background: Drawable)

}