package com.commonsense.android.kotlin.tools.anr

import android.os.Debug
import android.os.Handler
import android.os.Looper
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.Function2
import com.commonsense.android.kotlin.base.extensions.map
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.system.logging.L
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import java.lang.ref.WeakReference

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose: monitoring for ANR (application not responding).
 *
 */
object ANRWatcher {

    private var watcher: ANRWatcherThread = ANRWatcherThread("ANR watcher") {
        listener?.invoke()
    }

    var timeout: TimeUnit = TimeUnit.Milliseconds(5000)

    //<editor-fold desc="Enabled ability">
    var disableOnDebugger = false
        set(value) {
            field = value
            enabled = enabled //trigger a re-evaluation of the enabled property
        }

    var enabled: Boolean = false
        set(value) {
            field = value && (disableOnDebugger && !Debug.isDebuggerConnected() || !disableOnDebugger)
            watcher.setState(field)
        }
    //</editor-fold>

    //<editor-fold desc="Listener">
    var listener: EmptyFunction?
        set(value) {
            weakListener = value?.weakReference()
        }
        get() = weakListener?.get()

    private var weakListener: WeakReference<EmptyFunction>? = null
    //</editor-fold>

    //<editor-fold desc="Logging internally">
    var logTimings: Boolean
        get() = watcher.logTimings
        set(value) {
            watcher.logTimings = value
        }


    var logTimingsFunction: Function2<String, String, Unit>?
        get() = watcher.loggerFunction
        set(value) {
            watcher.loggerFunction = value
        }
    //</editor-fold>

}

private class ANRWatcherThread(val name: String, val callbackOnANR: EmptyFunction) {

    var logTimings: Boolean = true

    var loggerFunction: Function2<String, String, Unit>? = { tag, message ->
        L.error(tag, message)
    }

    private val mainLooper by lazy {
        Handler(Looper.getMainLooper())
    }
    private var job: Job? = null

    fun start() {
        job = async(newSingleThreadContext(name)) {
            while (this.isActive) {
                val start = System.currentTimeMillis()

                // delay(500)
                val end = async(UI) {
                    System.currentTimeMillis()
                }.await(ANRWatcher.timeout.toMilliseconds().milliSeconds - (System.currentTimeMillis() - start), null)

                val delta = end?.let {
                    TimeUnit.Milliseconds(it - start)
                }
                if (logTimings) {
                    logTiming(delta)
                }
                when {
                    delta == null -> callbackOnANR()
                    delta.milliSeconds > ANRWatcher.timeout.toMilliseconds().milliSeconds -> callbackOnANR()
                }
            }
        }
    }

    private fun logTiming(delta: TimeUnit.Milliseconds?) {
        launch(UI) {
            val message = delta.map("Ui thread response time was: $delta", "timed out, ANR detected")
            val tag = ANRWatcherThread::class.java.simpleName
            loggerFunction?.invoke(tag, message)
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

suspend fun <T> Deferred<T>.await(timeout: Long, defaultValue: T) =
        withTimeoutOrNull(timeout) { await() } ?: defaultValue