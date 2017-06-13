package com.commonsense.android.kotlin.android.widgets

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.android.extensions.getColorSafe
import com.commonsense.android.kotlin.android.extensions.getDrawableSafe
import com.commonsense.android.kotlin.android.widgets.base.ViewAttribute
import com.commonsense.android.kotlin.android.widgets.base.prepareAttributes
import com.commonsense.android.kotlin.collections.UpdateVariable
import com.commonsense.kotlin.R

/**
 * Created by Kasper Tvede on 13-06-2017.
 */
class ToggleImageButton : AppCompatImageView, ViewAttribute, View.OnClickListener {

    private val noColor = 0

    constructor(context: Context) : super(context) {
        prepareAttributes(this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        prepareAttributes(this, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        prepareAttributes(this, attrs, defStyleAttr)
    }

    private val internalChecked by lazy {
        UpdateVariable(false, this::updateView)
    }

    private val internalSelectedColor by lazy {
        UpdateVariable(noColor, this::updateView)
    }

    private val internalUnselectedColor by lazy {
        UpdateVariable(noColor, this::updateView)
    }


    var isChecked
        get() = internalChecked.value
        set(value) {
            internalChecked.value = value
        }


    var selectedColor
        get() = internalSelectedColor.value
        set(value) {
            internalSelectedColor.value = value
        }

    var unselectedColor
        get() = internalUnselectedColor.value
        set(value) {
            internalUnselectedColor.value = value
        }

    var internalOnclickListener: OnClickListener? = null

    override fun getStyleResource(): IntArray? {
        return R.styleable.ToggleImageButton
    }

    override fun parseTypedArray(data: TypedArray) {
        internalSelectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_selectedColor, noColor))
        internalUnselectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_unselectedColor, noColor))
    }

    override fun updateView() {
        val color = if (isChecked) {
            selectedColor
        } else {
            unselectedColor
        }
        if (color != noColor) {
            DrawableCompat.setTint(drawable, color)
        } else {
            DrawableCompat.setTint(drawable, noColor)
        }
        invalidate()
    }

    override fun afterSetupView() {
        super.setOnClickListener(this)
        if (background == null) {
            background = context.getDrawableSafe(R.drawable.card_bg)
        }

    }

    override fun onClick(sender: View?) {
        isChecked = !isChecked
    }


    override fun setOnClickListener(newOnclickListener: OnClickListener?) {
        internalOnclickListener = newOnclickListener
    }


}