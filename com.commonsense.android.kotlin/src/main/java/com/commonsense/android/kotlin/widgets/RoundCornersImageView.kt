package com.commonsense.android.kotlin.widgets

import android.support.v7.widget.AppCompatImageView

/**
 * Created by kasper on 29/05/2017.
 */
class RoundCornersImageView : AppCompatImageView {

    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {
        setupAttrs(attrs)
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupAttrs(attrs)
    }

    fun setupAttrs(attrs: android.util.AttributeSet) {

    }

    override fun onDraw(canvas: android.graphics.Canvas?) {
        super.onDraw(canvas)
    }
}
