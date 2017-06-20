package com.commonsense.android.kotlin.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
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
open class ToggleImageButton : AppCompatImageView, ViewAttribute, View.OnClickListener {

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

    private var onCheckedChanged: ((Boolean) -> Unit)? = null

    private val internalChecked by lazy {
        UpdateVariable(false, this::updateView)
    }

    private val internalSelectedColor by lazy {
        UpdateVariable(noColor, this::updateView)
    }

    private val internalUnselectedColor by lazy {
        UpdateVariable(noColor, this::updateView)
    }


    private val internalBackgroundSelected by lazy {
        UpdateVariable(background, this::updateView)
    }

    private val internalBackgroundUnselected by lazy {
        UpdateVariable(background, this::updateView)
    }

    var isChecked
        get() = internalChecked.value
        set(value) {
            onCheckedChanged?.invoke(value)
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

    var selectedBackground: Drawable?
        get() = internalBackgroundSelected.value
        set(value) {
            internalBackgroundSelected.value = value
        }

    var unselectedBackground: Drawable?
        get() = internalBackgroundUnselected.value
        set(value) {
            internalBackgroundUnselected.value = value
        }

    var internalOnclickListener: OnClickListener? = null

    override fun getStyleResource(): IntArray? {
        return R.styleable.ToggleImageButton
    }

    override fun parseTypedArray(data: TypedArray) {
        //colors
        internalSelectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_selectedColor, noColor))
        internalUnselectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_unselectedColor, noColor))
        //background
        internalBackgroundSelected.setWithNoUpdate(data.getDrawableSafe(R.styleable.ToggleImageButton_selectedBackground, context))
        internalBackgroundUnselected.setWithNoUpdate(data.getDrawableSafe(R.styleable.ToggleImageButton_unselectedBackground, context))

    }

    override fun updateView() {

        DrawableCompat.setTint(drawable.mutate(), getCheckColor())
        val checkBackground = getCheckBackground()
        if (checkBackground != background) {
            background = checkBackground
        }
        invalidate()
    }

    fun getCheckColor(): Int {
        return if (isChecked) {
            selectedColor
        } else {
            unselectedColor
        }
    }

    fun getCheckBackground(): Drawable? {
        return if (isChecked) {
            selectedBackground
        } else {
            unselectedBackground
        }
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


    fun setOnCheckedChangListener(onCheckedChanged: (Boolean) -> Unit) {
        this.onCheckedChanged = onCheckedChanged
    }

    var checkedNoNotify: Boolean
        get() = internalChecked.value
        set(value) {
            internalChecked.value = value
        }


}