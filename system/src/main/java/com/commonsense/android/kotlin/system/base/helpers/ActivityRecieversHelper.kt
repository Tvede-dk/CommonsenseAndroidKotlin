package com.commonsense.android.kotlin.system.base.helpers

import android.content.BroadcastReceiver
import android.content.Context
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifFalse
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue

/**
 * Created by Kasper Tvede on 04-12-2017.
 */
public class ActivityRecieversHelper {

    private val registeredListeners: MutableList<BroadcastReceiver?> = mutableListOf()
    public var isEnabled: Boolean = true
        set(value) {
            field = value.ifFalse(this::cleanup)
        }

    private fun cleanup() {
        registeredListeners.clear()
    }

    fun registerReceiver(receiver: BroadcastReceiver?) = useIfEnabled {
        registeredListeners.add(receiver)
    }

    fun unregisterReceiver(receiver: BroadcastReceiver?) {
        registeredListeners.remove(receiver)
    }

    fun onDestroy(context: Context) {
        useIfEnabled {
            registeredListeners.forEach(context::unregisterReceiver)
        }
        cleanup()
    }

    inline fun useIfEnabled(crossinline action: EmptyFunction) {
        isEnabled.ifTrue(action)
    }

}