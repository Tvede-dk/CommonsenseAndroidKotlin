package com.commonsense.android.kotlin.views.widgets

import android.text.InputFilter
import android.support.v7.widget.AppCompatEditText
import android.text.TextWatcher

/**
 * Created by kasper on 10/07/2017.
 */
class ExtendedEditTextView : AppCompatEditText {
    constructor(context: android.content.Context) : super(context) {
        afterInit()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {
        afterInit()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        afterInit()
    }


    private val ourFilter =
            InputFilter { charSequence, start, end, dest, dStart, dEnd ->
                if (end == 0) {
                    backspaceDetectedCallback?.invoke(this, text.isEmpty())
                }
                return@InputFilter charSequence
            }


    private fun afterInit() {
        filters = null
    }

    private val internalListOfListeners = mutableListOf<TextWatcher>()

    private var backspaceDetectedCallback: ((view: ExtendedEditTextView, isEmpty: Boolean) -> Unit)? = null

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

    override fun setFilters(filters: Array<out InputFilter>?) {
        val array: Array<out InputFilter> = if (filters != null) {
            arrayOf(getOurInputFilter(), *filters)
        } else {
            arrayOf(getOurInputFilter())
        }
        super.setFilters(array)
    }


    private fun getOurInputFilter(): InputFilter {
        return InputFilter { charSequence, start, end, dest, dStart, dEnd ->
            if (end == 0) {
                backspaceDetectedCallback?.invoke(this, text.isEmpty())
            }
            return@InputFilter charSequence
        }
    }

    fun setOnBackSpaceDeteced(callback: (view: ExtendedEditTextView, isEmpty: Boolean) -> Unit) {
        backspaceDetectedCallback = callback
    }
}
