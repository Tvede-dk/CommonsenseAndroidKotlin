@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
package com.commonsense.android.kotlin.base.concurrency

import kotlinx.coroutines.channels.*

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
class LimitedCoroutineCounter(maxCounter: Int) {

    private val channel = Channel<Unit>(maxCounter)

    suspend fun <T> perform(action: suspend () -> T): T {
        channel.send(Unit)
        try {
            return action()
        } finally {
            channel.receive()
        }
    }
}