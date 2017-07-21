package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.base.EmptyFunction

/**
 * Created by kasper on 21/07/2017.
 */

class ToggleBoolean(initialValue: Boolean) {
    private var isToggled = initialValue
    fun ifTrue(action: EmptyFunction) {
        if (isToggled) {
            isToggled = false
            action()
        }
    }

    fun ifFalse(action: EmptyFunction) {
        if (!isToggled) {
            isToggled = true
            action()
        }
    }
}