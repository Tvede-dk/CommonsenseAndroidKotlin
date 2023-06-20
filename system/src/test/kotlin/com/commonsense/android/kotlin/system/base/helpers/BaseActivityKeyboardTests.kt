package com.commonsense.android.kotlin.system.base.helpers

import android.app.*
import android.widget.*
import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.robolectric.*
import org.robolectric.annotation.*

/**
 * Created by kasper on 18/12/2017.
 */


@Config(manifest = Config.NONE)
class BaseActivityKeyboardTests : BaseRoboElectricTest() {


    @Test
    // @Ignore
    fun testHideKeyboard() {
        val helper = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).create()
        val act = helper.get()
        //the expectation
        act.keyboardHandler.isEnabled.assert(true)
        act.keyboardHandler.hideOnPause.assert(true)
        val editView = EditText(act)
        act.setContentView(editView)
        editView.requestFocusAndShowKeyboard()
        assertKeyboardVisible(act)

        act.hideSoftKeyboard()
        assertKeyboardHidden(act)

        editView.requestFocusAndShowKeyboard()
        assertKeyboardVisible(act)
        helper.pause()
        assertKeyboardHidden(act)

        helper.resume()
        editView.requestFocusAndShowKeyboard()
        assertKeyboardVisible(act)

        helper.destroy()
        assertKeyboardHidden(act)

    }

    private fun assertKeyboardHidden(activity: Activity) {
        Shadows.shadowOf(activity.inputMethodManager).isSoftInputVisible.assert(false)
    }

    private fun assertKeyboardVisible(activity: Activity) {
        Shadows.shadowOf(activity.inputMethodManager).isSoftInputVisible.assert(true)
    }

}