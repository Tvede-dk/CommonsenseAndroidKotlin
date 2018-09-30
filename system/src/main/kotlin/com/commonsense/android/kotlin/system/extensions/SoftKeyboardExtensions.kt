@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.app.*
import android.content.*
import android.view.inputmethod.*
import android.widget.*
import com.commonsense.android.kotlin.system.logging.*

/**
 * Created by Kasper Tvede on 28-05-2018.
 * Purpose:
 *
 */

inline fun Context.hideSoftKeyboardFrom(editText: EditText) {
    inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
}

inline fun Activity.hideSoftKeyboard() {
    val view = currentFocus ?: rootView
    if (view == null) {
        logWarning("no view in focus or root view")
        return
    }
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}


inline fun EditText.requestFocusAndShowKeyboard() {
    requestFocus()
    context.inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    postDelayed(this::setSelectionAtEnd, 16 * 2) //apprently android does this partially async /the above,
    //thus we cannot depend on sequential execution. (so focus will change selection)
}

