package com.commonsense.android.kotlin.widgets

import android.support.v7.widget.AppCompatEditText
import android.text.TextWatcher

/**
 * Created by kasper on 10/07/2017.
 */
class ExtendedEditTextView : AppCompatEditText {
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