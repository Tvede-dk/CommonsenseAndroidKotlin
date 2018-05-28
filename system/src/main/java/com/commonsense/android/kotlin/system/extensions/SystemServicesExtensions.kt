package com.commonsense.android.kotlin.system.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.commonsense.android.kotlin.system.logging.logWarning

/**
 * Created by Kasper Tvede on 21-05-2017.
 */

inline val Context.inputMethodManager: InputMethodManager?
    @SuppressLint("NewApi")
    get() {
        return if (isApiOverOrEqualTo(23)) {
            getSystemService(InputMethodManager::class.java)
        } else {
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        }
    }

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

inline val Activity.rootView: View?
    get() = window?.decorView?.rootView ?: findViewById(android.R.id.content)

@Suppress("NOTHING_TO_INLINE")
inline fun EditText.requestFocusAndShowKeyboard() {
    requestFocus()
    context.inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    postDelayed(this::setSelectionAtEnd, 16 * 2) //apprently android does this partially async /the above,
    //thus we cannot depend on sequential execution. (so focus will change selection)
}

