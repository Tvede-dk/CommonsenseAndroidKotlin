package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by kasper on 25/08/2017.
 */
class ToggleSelectionViewHandlerTest {

    @Test
    fun testRegularSelections() {
        var innerCallback: FunctionUnit<Set<String>>? = null
        val handler = ToggleSelectionViewHandler<String> {
            innerCallback?.invoke(it)
        }
        var callback1: SelectionToggleCallback<String>? = null
        var callback2: SelectionToggleCallback<String>? = null
        val view1 = object : ToggleableView<String> {
            override fun clearOnSelectionChanged() {

            }

            override val value: String
                get() = "1"

            override fun setOnSelectionChanged(callback: SelectionToggleCallback<String>) {
                callback1 = callback
            }

            override var checked: Boolean = false
        }

        val view2 = object : ToggleableView<String> {
            override fun clearOnSelectionChanged() {

            }

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
        innerCallback = {
            it.assertSize(0, "empty should be empty")
        }
        handler.selectValues(emptySet())

        innerCallback = {
            it.assertSize(2, "both should be selected")
            it.first().assert("1")
            it.last().assert("2")
        }
        handler.selectValues(setOf("1", "2"))

        innerCallback = {
            it.assertSize(1, "first should be selected")
            it.first().assert("1")
        }
        handler.selectValues(setOf("1"))

        innerCallback = {
            it.assertSize(1, "last should be selected")
            it.first().assert("2")
        }
        handler.selectValues(setOf("2"))


        //test the interactivity
        innerCallback = null
        handler.selectValues(emptySet())



        innerCallback = {
            it.assertSize(1)
            it.first().assert("1")
        }
        //simulate a selection
        callback1.assertNotNullApply { invoke(view1, true) }

        innerCallback = {
            it.assertSize(2)
        }
        callback2.assertNotNullApply { invoke(view2, true) }

        innerCallback = {
            it.assertSize(1)
        }
        callback2.assertNotNullApply { invoke(view2, false) }

        innerCallback = {
            it.assertSize(0)
        }
        callback1.assertNotNullApply { invoke(view1, false) }

    }

    @Ignore
    @Test
    fun getSelection() {
    }

    @Ignore
    @Test
    fun setSelection() {
    }

    @Ignore
    @Test
    fun handleSelectionChanged() {
    }

    @Ignore
    @Test
    fun isSelected() {
    }

    @Ignore
    @Test
    fun removeSelected() {
    }

    @Ignore
    @Test
    fun selectValues() {
    }

}