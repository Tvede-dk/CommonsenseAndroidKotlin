package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.system.extensions.getDrawableSafe
import com.commonsense.android.kotlin.system.imaging.withTintColor
import com.commonsense.android.kotlin.views.R
import com.commonsense.android.kotlin.views.datastructures.ColorValueViewVariable
import com.commonsense.android.kotlin.views.datastructures.UpdateVariable
import com.commonsense.android.kotlin.views.datastructures.ViewVariable
import java.lang.ref.WeakReference

/**
 * Created by Kasper Tvede on 13-06-2017.
 */
open class ToggleImageButton : AppCompatImageView, ViewAttribute, View.OnClickListener {

    private val noColor = 0

    //TODO move this ...
    private val listOfCustomProperties = mutableListOf<WeakReference<ViewVariable<*>>>()

    //<editor-fold desc="Constructors">
    constructor(context: Context) : super(context) {
        prepareAttributes()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        prepareAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        prepareAttributes(attrs, defStyleAttr)

    }
    //</editor-fold>


    //<editor-fold desc="Selected color">
    private val internalSelectedColor by lazy {
        ColorValueViewVariable(noColor, R.styleable.ToggleImageButton_selectedColor, listOfCustomProperties, this::updateView)
    }

    val selectedColor: Int by internalSelectedColor
    //</editor-fold>


    private var onCheckedChanged: ((Boolean) -> Unit)? = null

    private val internalChecked by lazy {
        UpdateVariable(false, this::updateView)
    }

    private val internalUnselectedColor by lazy {
        ColorValueViewVariable(noColor, R.styleable.ToggleImageButton_unselectedColor, listOfCustomProperties, this::updateView)
    }

    val unselectedColor: Int by internalSelectedColor

    private val internalBackgroundSelected by lazy {
        UpdateVariable(background, this::updateView)
    }

    private val internalBackgroundUnselected by lazy {
        UpdateVariable(background, this::updateView)
    }

    var isChecked
        get() = internalChecked.value
        set(value) {
            internalChecked.value = value
            onCheckedChanged?.invoke(value)
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

    override fun getStyleResource(): IntArray? = R.styleable.ToggleImageButton

    override fun parseTypedArray(data: TypedArray) {

        listOfCustomProperties.forEach {
            it.get()?.parse(data, context)
        }
        //colors
//        internalSelectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_selectedColor, noColor))
//        internalUnselectedColor.setWithNoUpdate(data.getColorSafe(R.styleable.ToggleImageButton_unselectedColor, noColor))
        //background
        internalBackgroundSelected.setWithNoUpdate(data.getDrawableSafe(R.styleable.ToggleImageButton_selectedBackground, context))
        internalBackgroundUnselected.setWithNoUpdate(data.getDrawableSafe(R.styleable.ToggleImageButton_unselectedBackground, context))

    }

    override fun updateView() {
        setImageDrawable(drawable.withTintColor(getCheckColor()))
        val checkBackground = getCheckBackground()
        if (checkBackground != background) {
            background = checkBackground
        }
        invalidate()
    }

    private fun getCheckColor(): Int
            = isChecked.map(selectedColor, unselectedColor)

    private fun getCheckBackground(): Drawable?
            = isChecked.map(selectedBackground, unselectedBackground)

    override fun afterSetupView() {
        super.setOnClickListener(this)
        background = background ?: context.getDrawableSafe(R.drawable.card_bg)
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