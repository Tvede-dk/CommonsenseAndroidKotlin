package com.commonsense.android.kotlin.views.input.selection

import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by kasper on 25/08/2017.
 */
class SingleSelectionHandlerTest {


    @Test
    fun testSelectionStrategy() {

        val selectionHandler = SingleSelectionHandler<String> {}
        val toggle1 = MockedToggleableViewNoCallback("1234")
        selectionHandler.addView(toggle1)
        toggle1.checked.assert(false, "should not be marked by default")
        selectionHandler.setSelectedValue("1234")
        toggle1.checked.assert(true, "should be marked when asked to")

        val toggle2 = MockedToggleableViewNoCallback("123")
        selectionHandler.addView(toggle2)
        toggle1.checked.assert(true)
        toggle2.checked.assert(false, "should not be marked by default")

        selectionHandler.setSelectedValue("123")
        toggle1.checked.assert(false)
        toggle2.checked.assert(true)

        val toggle3 = MockedToggleableViewNoCallback("abc")
        selectionHandler.addView(toggle3)
        toggle1.checked.assert(false)
        toggle2.checked.assert(true)
        toggle3.checked.assert(false)

        selectionHandler.setSelectedValue("1234")
        toggle1.checked.assert(true)
        toggle2.checked.assert(false)
        toggle3.checked.assert(false)

        selectionHandler.setSelectedValue("abc")
        toggle1.checked.assert(false)
        toggle2.checked.assert(false)
        toggle3.checked.assert(true)


    }

    @Test
    fun testCallbackFeature() {
        val selectionHandler = SingleSelectionHandler<String> {}
        val toggle1 = MockedToggleableViewWithCallback("1234")
        selectionHandler.addView(toggle1)
        toggle1.checked.assert(false, "should not be marked by default")
        selectionHandler.setSelectedValue("1234")
        toggle1.checked.assert(true, "should be marked when asked to")

        val toggle2 = MockedToggleableViewWithCallback("123")
        selectionHandler.addView(toggle2)
        toggle1.checked.assert(true)
        toggle2.checked.assert(false, "should not be marked by default")

        toggle2.checked = true
        toggle1.checked.assert(false)
        toggle2.checked.assert(true)

        toggle2.deselect()
        toggle1.checked.assert(false)
        toggle2.checked.assert(true)

        toggle1.checked = true
        toggle1.checked.assert(true)
        toggle2.checked.assert(false)

        toggle1.deselect()
        toggle1.checked.assert(true)
        toggle2.checked.assert(false)

        toggle2.deselect()
        toggle1.checked.assert(true)
        toggle2.checked.assert(false)

        val toggle3 = MockedToggleableViewWithCallback("qwerty")

        selectionHandler += toggle3
        toggle1.checked.assert(true)
        toggle2.checked.assert(false)
        toggle3.checked.assert(false)
        toggle3.checked = true
        toggle1.checked.assert(false)
        toggle2.checked.assert(false)
        toggle3.checked.assert(true)

        toggle3.select()
        toggle1.checked.assert(false)
        toggle2.checked.assert(false)
        toggle3.checked.assert(true)


        toggle2.select()
        toggle1.checked.assert(false)
        toggle2.checked.assert(true)
        toggle3.checked.assert(false)


    }

    @Test
    fun testCallback() {
        var ourValue: String? = null
        val selectionHandler = SingleSelectionHandler<String> {
            ourValue = it
        }
        val toggle1 = MockedToggleableViewWithCallback("1234")
        selectionHandler.addView(toggle1)
        toggle1.checked.assert(false)

        toggle1.checked = true
        "1234".assert(ourValue ?: "")
        ourValue = null
        (ourValue == null).assert(true)
        val toggle2 = MockedToggleableViewWithCallback("qwe")
        selectionHandler.addView(toggle2)
        (ourValue == null).assert(true)
        toggle2.checked = true

        "qwe".assert(ourValue!!)
    }


    @Test
    fun testDeselection() {
        var ourValue: String? = null
        val selectionHandler = SingleSelectionHandler<String> {
            ourValue = it
        }
        val toggle1 = MockedToggleableViewWithCallback("1234")
        selectionHandler += toggle1
        toggle1.checked = true
        "1234".assertNotNullAndEquals(ourValue)
        toggle1.checked = false
        "1234".assertNotNullAndEquals(ourValue)

        //before setup should still resepct it.
        selectionHandler.allowDeselection { ourValue = "<null>" }
        toggle1.checked = false
        "<null>".assertNotNullAndEquals(ourValue)
        toggle1.checked = true
        "1234".assertNotNullAndEquals(ourValue)
        toggle1.checked = false
        "<null>".assertNotNullAndEquals(ourValue)

        val toggle2 = MockedToggleableViewWithCallback("4321")
        selectionHandler += toggle2

        toggle2.checked = true
        toggle1.checked.assert(false)
        "4321".assertNotNullAndEquals(ourValue)

        toggle2.checked = false
        "<null>".assertNotNullAndEquals(ourValue)
        toggle1.checked.assert(false)
        toggle2.checked.assert(false)
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
    fun allowDeselection() {
    }

    @Ignore
    @Test
    fun setSelectedValue() {
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

}

private class MockedToggleableViewNoCallback(myStr: String) : ToggleableView<String> {
    override fun clearOnSelectionChanged() {

    }

    override var checked: Boolean = false
    override val value = myStr

    override fun setOnSelectionChanged(callback: SelectionToggleCallback<String>) {

    }

}

private class MockedToggleableViewWithCallback(myStr: String) : ToggleableView<String> {
    override fun clearOnSelectionChanged() {

    }

    private var _checked: Boolean = false

    override val value = myStr

    private var selectionCallback: SelectionToggleCallback<String>? = null

    override var checked: Boolean
        get() = _checked
        set(value) {
            _checked = value
            selectionCallback?.invoke(this, value)
        }

    override fun setOnSelectionChanged(callback: SelectionToggleCallback<String>) {
        this.selectionCallback = callback
    }

}