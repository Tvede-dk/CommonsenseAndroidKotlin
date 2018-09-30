package com.commonsense.android.kotlin.system.base.helpers

import android.app.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*

/**
 * Created by kasper on 15/12/2017.
 * Handles dismissing keyboard when switching screens .
 *
 */
class KeyboardHandlerHelper {


    /**
     * if false, nothing happens at lifecycle events
     */
    var isEnabled: Boolean = true

    var hideOnPause: Boolean = true


    fun onDestroy(activity: Activity) = ifIsEnabled {
        activity.hideSoftKeyboard()
    }

    fun onPause(activity: Activity) = ifIsEnabled {
        if (hideOnPause) {
            activity.hideSoftKeyboard()
        }
    }

    private inline fun ifIsEnabled(crossinline action: EmptyFunction) {
        isEnabled.ifTrue(action)
    }

    override fun toString(): String = toPrettyString()

    fun toPrettyString(): String {
        return "Keyboard handler state: " +
                "\n\t\tis enabled: $isEnabled" +
                "\n\t\tHide on pause: $hideOnPause"
    }
}