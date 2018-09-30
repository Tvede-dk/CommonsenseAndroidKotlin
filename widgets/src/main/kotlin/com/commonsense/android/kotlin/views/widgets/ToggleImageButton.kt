@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.widgets

import android.content.*
import android.graphics.drawable.*
import android.support.v7.widget.*
import android.util.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.imaging.*
import com.commonsense.android.kotlin.views.R
import com.commonsense.android.kotlin.views.datastructures.*
import com.commonsense.android.kotlin.views.input.selection.*
import java.lang.ref.*

/**
 * Created by Kasper Tvede on 13-06-2017.
 */
open class ToggleImageButton : AppCompatImageView, ViewAttribute, View.OnClickListener, CheckableStatusCallback {


    override val attributes: MutableList<WeakReference<ViewVariable<*>>> = mutableListOf()


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
        colorVariable(R.styleable.ToggleImageButton_selectedColor)
    }


    var selectedColor: Int by internalSelectedColor
    //</editor-fold>

    override fun setOnCheckedChangedListener(callback: EmptyFunction) {
        onCheckedChanged = callback
    }

    //<editor-fold desc="is Checked">
    private var onCheckedChanged: EmptyFunction?
        get() = internalChecked.onChanged
        set(value) {
            internalChecked.onChanged = value
        }

    private val internalChecked by lazy {
        booleanVariable(false, R.styleable.ToggleImageButton_checked)
    }
    override var checked by internalChecked
    //</editor-fold>

    //<editor-fold desc="unselected color">
    private val internalUnselectedColor by lazy {
        colorVariable(R.styleable.ToggleImageButton_unselectedColor)
    }

    var unselectedColor: Int by internalUnselectedColor
    //</editor-fold>


    //<editor-fold desc="selected background">
    private val internalBackgroundSelected by lazy {
        drawableVariable(R.styleable.ToggleImageButton_selectedBackground)
    }

    var selectedBackground: Drawable? by internalBackgroundSelected
    //</editor-fold>

    //<editor-fold desc="unselected background">
    private val internalBackgroundUnselected by lazy {
        drawableVariable(R.styleable.ToggleImageButton_unselectedBackground)
    }
    var unselectedBackground: Drawable? by internalBackgroundUnselected
    //</editor-fold>

    var internalOnclickListener: OnClickListener? = null

    override fun getStyleResource(): IntArray? = R.styleable.ToggleImageButton

    override fun updateView() {
        setImageDrawable(drawable?.withTintColor(getCheckColor()))
        val checkBackground = getCheckBackground()
        if (checkBackground != background) {
            background = checkBackground
        }
        invalidate()
    }

    private fun getCheckColor(): Int = checked.map(selectedColor, unselectedColor)

    private fun getCheckBackground(): Drawable? = checked.map(selectedBackground, unselectedBackground)

    override fun afterSetupView() {
        super.setOnClickListener(this)
        background = background ?: context.getDrawableSafe(R.drawable.card_bg)
    }

    override fun onClick(sender: View?) {
        checked = !checked
    }


    override fun setOnClickListener(newOnclickListener: OnClickListener?) {
        internalOnclickListener = newOnclickListener
    }

    override fun clearOnSelectionChanged() {
        this.onCheckedChanged = null
    }

    fun setOnCheckedChangedListener(onCheckedChanged: (Boolean) -> Unit) {
        this.onCheckedChanged = { onCheckedChanged(this.checked) }
    }

    var checkedNoNotify: Boolean
        get() = internalChecked.getInnerValue()
        set(value) {
            internalChecked.setNoUpdate(value)
            updateView()
        }

}