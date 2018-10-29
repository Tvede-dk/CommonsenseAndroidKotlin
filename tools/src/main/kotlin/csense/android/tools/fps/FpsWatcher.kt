@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package csense.android.tools.fps

import android.content.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.algorithms.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.time.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import java.lang.ref.*
import kotlin.math.*

/**
 * Created by Kasper Tvede½
 * Purpose:
 *
 */

typealias FpsWatcherCallback = (currentFps: Float,
                                droppedFrames: Float,
                                overdueTime: TimeUnit) -> Unit

class FpsWatcher(context: Context,
                 onFpsCallback: FpsWatcherCallback) {

    private val weakContext: WeakReference<Context> = context.weakReference()
    private val weakCallback: WeakReference<FpsWatcherCallback> = onFpsCallback.weakReference()

    //this serves as a default, and only used if we cannot retrieve it though android;
    //since most displays are somewhere around 60 hz, we assume this is the default.
    private val defaultFps: Float = 60f

    private val runningAverage = RunningAverageFloatCapped(10)

    /**
     * try limit to fps, so we do not overload ui thread
     */
    private val backoffTimingInNs by lazy {
        TimeUnit.MilliSeconds(
                TimeUnit.Seconds(1).toMilliSeconds()
                        .value / defaultFps.roundToInt())
                .toNanoSeconds()
                .value
    }

    private var lastAverage: Float = 0f

    private var lastTime: Long = 0

    /**
     * how many milliseconds there are in a second.
     */
    private val secondInMillisecond: Long = TimeUnit.Seconds(1)
            .toMilliSeconds()
            .value


    /**
     * The displays refresh rate
     */
    private val fpsForDefaultScreen: Float = context.defaultDisplay?.refreshRate
            ?: defaultFps

    /**
     * The callbacker that does all the passing forth of timings.
     */
    private val choreographerCallback: FpsCallback by lazy { FpsCallback(this::onTiming) }

    /**
     * Starts the measurement
     */
    fun start() {
        runningAverage.reset()
        choreographerCallback.start()
        Choreographer.getInstance().postFrameCallback(choreographerCallback)
    }

    /**
     * Stops the measurement.
     */
    fun stop() {
        choreographerCallback.stop()
        runningAverage.reset()
    }

    /**
     * we get called with the timing difference between 2 frames.
     * kinda like
     * [start frame ....... start frame ]
     * and then the delta which is
     * [.......]; which determines the "fps" the time between frames .
     * the amount off dropped / missed frames is then given by the expected fps vs the actual.
     *
     * Does the math, then afterwards schedules the result on the ui thread.
     */
    private fun onTiming(timeInNs: Long) {

        val callbackOnTiming = weakCallback.get()
        if (weakContext.get().isNull) {
            L.debug(FpsWatcher::class.java.simpleName, "detected context is null akk starter is dead, so we should close.")
            stop()
            return
        }

        if (callbackOnTiming == null) {
            L.debug(FpsWatcher::class.java.simpleName, "callback is null so we are unable to get back in touch")
            stop()
            return
        }

        val timeInMs = timeInNs / 1_000_000
        val currentFps = secondInMillisecond / timeInMs.toFloat()
        val expectedFps = fpsForDefaultScreen
        if (currentFps > expectedFps * 1.1f) {
            L.debug(FpsWatcher::class.java.simpleName, "$currentFps is over the expected Fps, thus skipping.")
            return //bad measuring.
        }
        val missedTiming = (secondInMillisecond / fpsForDefaultScreen) - timeInMs
        runningAverage.addValue(currentFps)
        val currentTime = System.nanoTime()
        val average = runningAverage.average().toFloat()

        if (currentTime - lastTime > backoffTimingInNs && !average.equals(lastAverage, 0.1f)) {
            lastTime = currentTime
            lastAverage = average
            GlobalScope.launch(Dispatchers.Main) {
                L.error(FpsWatcher::class.java.simpleName, "fps is:$currentFps")
                callbackOnTiming(average,
                        maxOf(expectedFps - currentFps, 0f),
                        TimeUnit.MilliSeconds(missedTiming.toLong()))
            }
        }
    }
}

class FpsCallback(
        onTiming: FunctionUnit<Long>) : Choreographer.FrameCallback {

    private val weakCallback: WeakReference<FunctionUnit<Long>> = onTiming.weakReference()

    private var lastTime: Long? = null
    fun start() {
        isEnabled = true
        lastTime = null
    }

    fun stop() {
        isEnabled = false
        lastTime = null
    }

    private var isEnabled: Boolean = false

    /**
     * Called each time the ui is rendered.
     */
    override fun doFrame(frameTimeNanos: Long) {
        if (!isEnabled || weakCallback.get().isNull) {
            L.error("test", "doframe is canceling.")
            Choreographer.getInstance().removeFrameCallback(this)
            return
        }
        val currentLastTime = lastTime
        //if we have a time, then use it. otherwise wait for a second measurement.
        if (currentLastTime != null) {
            val delta = frameTimeNanos - currentLastTime
            weakCallback.useRefOr(ifAvailable = {
                this(delta)
                //reschedule our self.
            }, ifNotAvailable = {
                L.error(FpsCallback::class.java.simpleName, "callback from do frame is GC'ed...")
            })
        }
        lastTime = frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }

}