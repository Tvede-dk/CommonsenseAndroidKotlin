package com.commonsense.android.kotlin.system.extensions

import android.widget.EditText

/**
 * Created by Kasper Tvede on 03-08-2017.
 */


fun EditText.setSelectionAtEnd() {
    setSelection(length())
}