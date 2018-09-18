package com.commonsense.android.kotlin.system.base.helpers

import android.content.BroadcastReceiver
import android.content.Context
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.base.extensions.collections.ifFalse
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.logging.tryAndLog

/**
 * A helper for handling BroadCastReceiver registrations of an activity.
 */
class ActivityReceiversHelper {

    /**
     * a list of registered receivers.
     */
    private val registeredListeners: MutableList<BroadcastReceiver> = mutableListOf()

    /**
     *
     */
    var isEnabled: Boolean = true
        set(value) {
            field = value.ifFalse(this::cleanup)
        }

    /**
     * Removes all the internal data
     */
    private fun cleanup() {
        registeredListeners.clear()
    }

    /**
     *
     * @param receiver BroadcastReceiver?
     */
    fun registerReceiver(receiver: BroadcastReceiver?) = useIfEnabled {
        receiver?.let {
            registeredListeners.add(it)
        }
    }

    /**
     *
     * @param receiver BroadcastReceiver?
     */
    fun unregisterReceiver(receiver: BroadcastReceiver?) {
        registeredListeners.remove(receiver)
    }

    /**
     *
     * @return List<BroadcastReceiver>
     */
    fun listReceivers(): List<BroadcastReceiver> = registeredListeners

    fun onDestroy(context: Context) {
        useIfEnabled {
            tryAndLog(ActivityReceiversHelper::class) {
                //toList to make sure if we call our self that we have no concurrent exceptions
                registeredListeners.toList().forEach {
                    context.unregisterReceiver(it)
                }
            }
        }
        cleanup()
    }

    /**
     * calls the given action iff isEnabled is true, does nothing otherwise
     * @param action EmptyFunction
     */
    private inline fun useIfEnabled(crossinline action: EmptyFunction) {
        isEnabled.ifTrue(action)
    }

    override fun toString(): String = toPrettyString()
    /**
     * Creates a pretty string representation of this object
     * @return String
     */
    fun toPrettyString(): String {
        return "Activity receivers helper state" +
                "\n\t\tis enabled: $isEnabled\n\t\t" +
                registeredListeners.map { "$it" }.prettyStringContent(
                        "registered broadcast receivers",
                        "no broadcast receivers registered on this activity")
    }


}