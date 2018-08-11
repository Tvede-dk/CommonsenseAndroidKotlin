package csense.android.tools.anr

import android.os.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.mapLazy
import com.commonsense.android.kotlin.base.time.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*
import java.lang.ref.*

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose: monitoring for ANR (application not responding).
 *
 */
object ANRWatcher {

    private val watcher: ANRWatcherThread by lazy {
        ANRWatcherThread("ANR watcher") {
            listener?.invoke()
        }
    }

    var timeout: TimeUnit = TimeUnit.MilliSeconds(5000)
        set(value) {
            watcher.currentTimeoutInMs = value.toMilliSeconds()
            field = value
        }

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
    private var job: Job? = null

    fun start() {
        job = async(newSingleThreadContext(name)) {
            while (this.isActive) {

                val start = System.currentTimeMillis()

                val end = monitorWith()

                val delta = end?.let {
                    TimeUnit.MilliSeconds(it - start)
                }
                if (logTimings) {
                    logTiming(delta)
                }
                when {
                    delta == null -> callbackOnANR()
                    delta.isTimeout() -> callbackOnANR()
                    //try beeing nice on the device, by not spaming the UI thread massively. so make sure we skip a few frames here and there..

                    else -> delta.relaxSpamming()
                }
            }
        }
    }

    /**
     * makes sure that if we are not using more than 50 ms per iteration, then we wait approx 100 ms
     * between "tests".
     * should free up some cpu time and avoid doing too much work on the UI thread / and or the CPU.
     *
     * @receiver TimeUnit.MilliSeconds
     */
    private suspend fun TimeUnit.MilliSeconds.relaxSpamming() {
        L.warning(ANRWatcher::class,"before relax")
        if (value < 50) {
            delay(100 - value)
        }
        L.warning(ANRWatcher::class,"after relax")
    }

    /**
     * Partially bussy waiting loop.
     * @return Long?
     */
    suspend fun monitorWith(): Long? {
        var calledBack: Long? = null

        launch(UI) {
            L.debug(ANRWatcher::class, "measuring")
            calledBack = System.currentTimeMillis()
        }
        val timeoutInMs = currentTimeoutInMs.value

        if (timeoutInMs < 100) {
            delay(100, java.util.concurrent.TimeUnit.MILLISECONDS)
        } else {
            for (x in 0 until (offset + 1)) {
                delay(offset)
                if (calledBack != null) {
                    return calledBack
                }
            }
        }
        return calledBack

    }

    private fun logTiming(delta: TimeUnit.MilliSeconds?) {
        launchBlock(UI) {
            val tag = ANRWatcherThread::class.java.simpleName
            val message = if (delta == null || delta.isTimeout()) {
                "timed out, ANR detected, delta (if present) is: $delta"
            } else {
                "Ui thread response time was: $delta"
            }
            loggerFunction?.invoke(tag, message)
        }
    }

    fun TimeUnit.MilliSeconds.isTimeout(): Boolean {
        return ANRWatcher.timeout.toMilliSeconds().value <= this.value + offset2
    }

    fun stop() {
        job?.cancel()
    }

    fun setState(start: Boolean) =
            start.mapLazy(::start, ::stop)

    internal var currentTimeoutInMs: TimeUnit.MilliSeconds = ANRWatcher.timeout.toMilliSeconds()
        set(value) {
            currentTimeoutInMs.dividerOffset
            offset = currentTimeoutInMs.value / dividerOffset
            offset2 = offset * 2
            field = value
        }

    private var dividerOffset = currentTimeoutInMs.dividerOffset

    private var offset = currentTimeoutInMs.value / dividerOffset

    private var offset2 = (currentTimeoutInMs.value / dividerOffset) * 2

    private val TimeUnit.MilliSeconds.dividerOffset
        get () = (value > 1000).map(50, 5)


}

//suspend fun <T> Deferred<T>.await(timeout: Long, defaultValue: T) =
//        withTimeoutOrNull(timeout) { await() } ?: defaultValue