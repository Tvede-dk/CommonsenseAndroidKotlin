package com.commonsense.android.kotlin.system.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.commonsense.android.kotlin.system.logging.logWarning

/**
 * Created by Kasper Tvede on 28-05-2018.
 * Purpose:
 *
 */

@Suppress("NOTHING_TO_INLINE")
inline fun Context.hideSoftKeyboardFrom(editText: EditText) {
    inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Activity.hideSoftKeyboard() {
    val view = currentFocus ?: rootView
    if (view == null) {
        logWarning("no view in focus or root view")
        return
    }
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}


@Suppress("NOTHING_TO_INLINE")
inline fun EditText.requestFocusAndShowKeyboard() {
    requestFocus()
    context.inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    postDelayed(this::setSelectionAtEnd, 16 * 2) //apprently android does this partially async /the above,
    //thus we cannot depend on sequential execution. (so focus will change selection)
}

