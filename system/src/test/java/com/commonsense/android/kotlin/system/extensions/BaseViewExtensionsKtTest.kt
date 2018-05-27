package com.commonsense.android.kotlin.system.extensions

import android.widget.EditText
import com.commonsense.android.kotlin.base.extensions.toEditable
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Kasper Tvede on 27-05-2018.
 * Purpose:
 */
class BaseViewExtensionsKtTest : BaseRoboElectricTest() {

    @Test
    fun setSelectionAtEnd() {

        val editText = EditText(context)
        editText.text = "a long text".toEditable()
        //make sure we are not at the end.
        editText.setSelection(0)
        editText.selectionStart.assert(0, "should have selection at start after setting text")
        editText.selectionEnd.assert(0, "selection length should be 0 akk start == end")
        editText.setSelectionAtEnd()
        editText.selectionStart.assert(editText.length(), "should have placed selection at end")
        editText.selectionEnd.assert(editText.length(), "selection length should be 0 akk start == end")

    }

    @Test
    fun setSelectionAtStart() {
        val editText = EditText(context)
        editText.text = "a long text 42 ^ 42 ".toEditable()
        //make sure we are not at the start.
        editText.setSelectionAtEnd()
        editText.selectionStart.assert(editText.length())
        editText.selectionEnd.assert(editText.length())

        editText.setSelectionAtStart()
        editText.selectionStart.assert(0)
        editText.selectionEnd.assert(0)
    }
}