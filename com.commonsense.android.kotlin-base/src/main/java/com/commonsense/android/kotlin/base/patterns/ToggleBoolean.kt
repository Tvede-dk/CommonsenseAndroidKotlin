package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.base.EmptyFunction

/**
 * Created by kasper on 21/07/2017.
 *
 * Gets toggled every time you "use" it.
 */
class ToggleBoolean(initialValue: Boolean) {

    private var isToggled = initialValue

    fun ifTrue(action: EmptyFunction) {
        if (isToggled) {
            toggleAndRun(action)
        }
    }

    fun ifFalse(action: EmptyFunction) {
        if (!isToggled) {
            toggleAndRun(action)
        }
    }

    private inline fun toggleAndRun(crossinline action: EmptyFunction) {
        isToggled = !isToggled
        action()
    }
}