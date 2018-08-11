package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by kasper on 25/08/2017.
 */
class ToggleSelectionViewHandlerTest {

    @Test
    fun testRegularSelections() {
        val handler = ToggleSelectionViewHandler<String>()
        var callback1: SelectionToggleCallback<String>? = null
        var callback2: SelectionToggleCallback<String>? = null
        val view1 = object : ToggleableView<String> {
            override val value: String
                get() = "1"

            override fun setOnSelectionChanged(callback: SelectionToggleCallback<String>) {
                callback1 = callback
            }

            override var checked: Boolean = false
        }

        val view2 = object : ToggleableView<String> {
            override val value: String
                get() = "2"

            override fun setOnSelectionChanged(callback: SelectionToggleCallback<String>) {
                callback2 = callback
            }

            override var checked: Boolean = false
        }
        handler += view1
        handler += view2


        //test the selection functionality
        handler.callback = {
            it.assertSize(0, "empty should be empty")
        }
        handler.selectValues(emptySet())

        handler.callback = {
            it.assertSize(2, "both should be selected")
            it.first().assert("1")
            it.last().assert("2")
        }
        handler.selectValues(setOf("1", "2"))

        handler.callback = {
            it.assertSize(1, "first should be selected")
            it.first().assert("1")
        }
        handler.selectValues(setOf("1"))

        handler.callback = {
            it.assertSize(1, "last should be selected")
            it.first().assert("2")
        }
        handler.selectValues(setOf("2"))


        //test the interactivity
        handler.callback = null
        handler.selectValues(emptySet())



        handler.callback = {
            it.assertSize(1)
            it.first().assert("1")
        }
        //simulate a selection
        callback1.assertNotNullApply { invoke(view1, true) }

        handler.callback = {
            it.assertSize(2)
        }
        callback2.assertNotNullApply { invoke(view2, true) }

        handler.callback = {
            it.assertSize(1)
        }
        callback2.assertNotNullApply { invoke(view2, false) }

        handler.callback = {
            it.assertSize(0)
        }
        callback1.assertNotNullApply { invoke(view1, false) }

    }


}