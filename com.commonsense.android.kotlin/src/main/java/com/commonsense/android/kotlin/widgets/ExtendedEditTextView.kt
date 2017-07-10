package com.commonsense.android.kotlin.widgets

import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by kasper on 10/07/2017.
 */
class ExtendedEditTextView : EditText {
    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val internalListOfListeners = mutableListOf<TextWatcher>()


    fun setTextChangeListener(listener: TextWatcher) {
        clearTextChangeListeners()
        addTextChangedListener(listener)
        internalListOfListeners.add(listener)
    }

    fun clearTextChangeListeners() {
        internalListOfListeners.forEach { super.removeTextChangedListener(it) }
        internalListOfListeners.clear()
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        super.removeTextChangedListener(watcher)
        internalListOfListeners.remove(watcher)

    }


}