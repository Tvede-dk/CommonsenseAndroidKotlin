package com.commonsense.android.kotlin.base.concurrency

import android.util.Log
import kotlinx.coroutines.experimental.channels.Channel

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
class LimitedCoroutineCounter(maxCounter: Int) {
    private val channel = Channel<Unit>(maxCounter)
    suspend fun <T> perform(action: suspend () -> T): T {
        Log.w("omg", "isFull: ${channel.isFull}")
        channel.send(Unit)
        try {
            return action()
        } finally {
            channel.receive()
        }
    }

}