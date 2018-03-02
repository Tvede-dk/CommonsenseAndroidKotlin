package com.commonsense.android.kotlin.tools.anr

import android.os.Debug
import android.os.Handler
import android.os.Looper
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.tools.NotificationType
import com.commonsense.android.kotlin.tools.TimeUnit
import com.commonsense.android.kotlin.tools.delay
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose: monitoring for ANR (application not responding).
 *
 */
class ANRWatcher(val mediumThreadsholdTime: TimeUnit,
                 val mediumThreadsholdNotification: NotificationType,
                 val maxThreadsholdTime: TimeUnit,
                 val maxThreadsholdNotification: NotificationType) {

    companion object {
        private var watcher: ANRWatcherThread = ANRWatcherThread("ANR watcher", {
            listener?.invoke()
        })

        var disableOnDebugger = false

        var enabled: Boolean = false
            set(value) {
                field = value && (disableOnDebugger && !Debug.isDebuggerConnected() || !disableOnDebugger)
                watcher.setState(field)
            }

        var timeout: TimeUnit = TimeUnit.Milliseconds(5000)

        var listener: EmptyFunction? = null
    }
}

private class ANRWatcherThread(val name: String, val callbackOnANR: EmptyFunction) {

    private val mainLooper by lazy {
        Handler(Looper.getMainLooper())
    }
    private var job: Job? = null

    fun start() {
        job = async(newSingleThreadContext(name)) {

            while (this.isActive) {
                val delta = measureOptAsync(ANRWatcher.timeout, { result ->
                    mainLooper.post {
                        result(System.currentTimeMillis())
                    }
                })
                val haveSet = delta != null
                val isAboveTimeout = (delta?.getMilliseconds() ?: Long.MAX_VALUE) >
                        ANRWatcher.timeout.toMilliseconds().getMilliseconds()

                if (!haveSet || isAboveTimeout) {
                    callbackOnANR()
                }

            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun setState(start: Boolean) {
        if (start) {
            start()
        } else {
            stop()
        }
    }
}


/**
 * Measure the time of some occurence with a timeout. the function waits (coroutine delay) until the given timeout.
 *
 * @param waitingTimeUnit
 * @param signalAsync the schedualar to measure the time
 * @return the delta time for the start of the operation to the "optional" ending,
 * if the ending have not occurred then it returns null
 */
suspend inline fun measureOptAsync(waitingTimeUnit: TimeUnit,
                                   crossinline signalAsync: FunctionUnit<FunctionUnit<Long>>)
        : TimeUnit.Milliseconds? {
    var optionalEnd: Long = 0
    var didSet = false
    val start = System.currentTimeMillis()
    signalAsync({
        optionalEnd = it
        didSet = true
    })
    waitingTimeUnit.delay()
    return didSet.map(
            ifTrue = TimeUnit.Milliseconds(optionalEnd - start),
            ifFalse = null)
}

