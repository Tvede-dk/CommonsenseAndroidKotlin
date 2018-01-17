package com.commonsense.android.kotlin.system.base.helpers

import android.app.Activity
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.extensions.hideSoftKeyboard

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
}