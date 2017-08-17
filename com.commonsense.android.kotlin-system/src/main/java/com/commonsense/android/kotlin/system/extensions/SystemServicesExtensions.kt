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
    requestFocus()
    val service = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    service?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    postDelayed(this::setSelectionAtEnd, 16 * 2) //apprently android does this partially async /the above,
    //thus we cannot depend on sequential execution. (so focus will change selection)
}

