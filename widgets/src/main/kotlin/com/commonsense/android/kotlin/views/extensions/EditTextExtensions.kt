package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.*
import android.text.*
import android.view.inputmethod.*
import android.widget.*


/**
 * Marks an EditText to be the "last one" akk the one with the ime done action button on the keyboard.
 * @receiver EditText
 */
@UiThread
fun EditText.imeDone() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    inputType = inputType xor InputType.TYPE_TEXT_FLAG_MULTI_LINE xor InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
}
