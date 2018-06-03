package com.commonsense.android.kotlin.views.widgets

import android.support.v7.widget.AppCompatEditText
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.commonsense.android.kotlin.views.extensions.isEmpty

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


    private fun afterInit() {

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


    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        if (outAttrs == null) {
            throw RuntimeException("Bad editor info supplied;" +
                    "\n You have properly added the focusAble attribute to this view in the xml), just remove it. " +
                    "\n (reason for throwing: super call will fail with unhelpful exception in android code.)")
        }
        return KeyboardConnection(super.onCreateInputConnection(outAttrs), true, this::onKeyboardEvent)
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

private class KeyboardConnection(target: InputConnection,
                                 mutable: Boolean,
                                 val callback: onKeyboardEvent)
    : InputConnectionWrapper(target, mutable) {

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && callback(event)) {
            return true
        }
        return super.sendKeyEvent(event)

    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace, when there are no text..
        return if (beforeLength == 1 && afterLength == 0) {
            // emulate backspace event..
            sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        } else {
            super.deleteSurroundingText(beforeLength, afterLength)
        }

    }
}