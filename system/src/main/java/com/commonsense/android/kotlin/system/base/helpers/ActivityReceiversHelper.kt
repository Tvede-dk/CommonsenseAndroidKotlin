package com.commonsense.android.kotlin.system.base.helpers

import android.content.BroadcastReceiver
import android.content.Context
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifFalse
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.logging.tryAndLog

/**
 * Created by Kasper Tvede on 04-12-2017.
 */
class ActivityReceiversHelper {

    private val registeredListeners: MutableList<BroadcastReceiver> = mutableListOf()

    /**
     *
     */
    var isEnabled: Boolean = true
        set(value) {
            field = value.ifFalse(this::cleanup)
        }

    private fun cleanup() {
        registeredListeners.clear()
    }

    fun registerReceiver(receiver: BroadcastReceiver?) = useIfEnabled {
        receiver?.let {
            registeredListeners.add(it)
        }
    }

    fun unregisterReceiver(receiver: BroadcastReceiver?) {
        registeredListeners.remove(receiver)
    }

    fun listReceivers(): List<BroadcastReceiver> = registeredListeners

    fun onDestroy(context: Context) {
        useIfEnabled {
            tryAndLog(ActivityReceiversHelper::class.java.simpleName) {
                //toList to make sure if we call our self that we have no concurrent exceptions
                registeredListeners.toList().forEach {
                    context.unregisterReceiver(it)
                }
            }
        }
        cleanup()
    }

    private inline fun useIfEnabled(crossinline action: EmptyFunction) {
        isEnabled.ifTrue(action)
    }


}