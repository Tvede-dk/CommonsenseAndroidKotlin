@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.widgets

import android.text.*
import android.view.*
import android.view.inputmethod.*
import androidx.appcompat.widget.*
import com.commonsense.android.kotlin.views.extensions.*

/**
 * Created by kasper on 10/07/2017.
 */
class ExtendedEditTextView : AppCompatEditText {
    constructor(context: android.content.Context) : super(context) {
        afterInit()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(
        context,
        attrs
    ) {
        afterInit()
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        afterInit()
    }


    private fun afterInit() {

    }

    private val internalListOfListeners = mutableListOf<TextWatcher>()

    private var backspaceDetectedCallback: ((view: ExtendedEditTextView, isEmpty: Boolean) -> Unit)? =
        null

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


    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val superConnection = super.onCreateInputConnection(outAttrs) ?: return null
        return KeyboardConnection(superConnection, true, this::onKeyboardEvent)
    }

    //TODO listen for custom sequences ? (hmm ) would avoid opening this func.
    private fun onKeyboardEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
            backspaceDetectedCallback?.invoke(this, isEmpty)
        }
        return false
    }

    fun setOnBackSpaceDetected(callback: (view: ExtendedEditTextView, isEmpty: Boolean) -> Unit) {
        backspaceDetectedCallback = callback
    }

    //hardware keyboard
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && onKeyboardEvent(event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

typealias onKeyboardEvent = (KeyEvent) -> Boolean

private class KeyboardConnection(
    target: InputConnection,
    mutable: Boolean,
    val callback: onKeyboardEvent
) : InputConnectionWrapper(target, mutable) {

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && callback(event)) {
            return true
        }
        return super.sendKeyEvent(event)

    }
}