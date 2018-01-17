package com.commonsense.android.kotlin.system.base

import android.app.Activity
import android.widget.EditText
import com.commonsense.android.kotlin.system.BuildConfig
import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.system.extensions.hideSoftKeyboard
import com.commonsense.android.kotlin.system.extensions.inputMethodManager
import com.commonsense.android.kotlin.system.extensions.requestFocusAndShowKeyboard
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import org.junit.Ignore
import org.junit.Test
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Created by kasper on 18/12/2017.
 */


@Config(constants = BuildConfig::class, manifest = Config.NONE)
class BaseActivityKeyboardTests : BaseRoboElectricTest() {


    @Test
    @Ignore
    fun testHideKeyboard() {
        val helper = createActivityController<BaseActivity>(R.style.Theme_AppCompat).create()
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