package com.commonsense.android.kotlin.system.extensions

import android.widget.*

/**
 * Created by Kasper Tvede on 03-08-2017.
 */

/**
 * puts the selection at the end (no selection length though)
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.setSelectionAtEnd() {
    //setSelection to an index sets the selection to [index, index]
    setSelection(length())
}

/**
 * puts the selection at the start (no selection length though)
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.setSelectionAtStart() {
    setSelection(0)
}

