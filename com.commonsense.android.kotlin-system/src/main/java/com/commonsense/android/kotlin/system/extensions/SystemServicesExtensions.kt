package com.commonsense.android.kotlin.system.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by Kasper Tvede on 21-05-2017.
 */


fun Context.HideSoftKeyboardFrom(editText: EditText) {
    val service = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    service?.hideSoftInputFromWindow(editText.windowToken, 0)
}

fun EditText.requestFocusAndShowKeyboard() {
    val service = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    service?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}