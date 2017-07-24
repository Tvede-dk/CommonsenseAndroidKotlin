package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.system.extensions.getDrawableSafe
import com.commonsense.android.kotlin.system.imaging.withTintColor
import com.commonsense.android.kotlin.views.R
import com.commonsense.android.kotlin.views.datastructures.BooleanCallbackViewVariable
import com.commonsense.android.kotlin.views.datastructures.ColorValueViewVariable
import com.commonsense.android.kotlin.views.datastructures.DrawableViewVariable
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
        ColorValueViewVariable(noColor, R.styleable.ToggleImageButton_selectedColor,
                listOfCustomProperties, this::updateView)
    }


    var selectedColor: Int by internalSelectedColor
    //</editor-fold>


    //<editor-fold desc="isChecked">
    private var onCheckedChanged: EmptyFunction?
        get() = internalChecked.onChanged
        set(value) {
            internalChecked.onChanged = value
        }

    private val internalChecked by lazy {
        BooleanCallbackViewVariable(false, R.styleable.ToggleImageButton_checked,
                listOfCustomProperties, this::updateView)
    }
    var isChecked by internalChecked
    //</editor-fold>

    //<editor-fold desc="unselected color">
    private val internalUnselectedColor by lazy {
        ColorValueViewVariable(noColor, R.styleable.ToggleImageButton_unselectedColor, listOfCustomProperties, this::updateView)
    }

    var unselectedColor: Int by internalUnselectedColor
    //</editor-fold>


    //<editor-fold desc="selected background">
    private val internalBackgroundSelected by lazy {
        DrawableViewVariable(R.styleable.ToggleImageButton_selectedBackground, listOfCustomProperties,
                this::updateView)
    }

    var selectedBackground: Drawable? by internalBackgroundSelected
    //</editor-fold>

    //<editor-fold desc="unselected background">
    private val internalBackgroundUnselected by lazy {
        DrawableViewVariable(R.styleable.ToggleImageButton_unselectedBackground, listOfCustomProperties,
                this::updateView)
    }
    var unselectedBackground: Drawable? by internalBackgroundUnselected
    //</editor-fold>

    var internalOnclickListener: OnClickListener? = null

    override fun getStyleResource(): IntArray? = R.styleable.ToggleImageButton

    override fun parseTypedArray(data: TypedArray) {
        listOfCustomProperties.forEach {
            it.get()?.parse(data, context)
        }
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


    fun setOnCheckedChangedListener(onCheckedChanged: (Boolean) -> Unit) {
        this.onCheckedChanged = { onCheckedChanged(this.isChecked) }
    }

    var checkedNoNotify: Boolean
        get() = internalChecked.getInnerValue()
        set(value) = internalChecked.setNoUpdate(value)

}