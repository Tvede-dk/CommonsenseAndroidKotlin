package com.commonsense.android.kotlin.tools.fps

import android.content.Context
import android.view.Choreographer
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.algorithms.RunningAverageFloatCapped
import com.commonsense.android.kotlin.base.extensions.equals
import com.commonsense.android.kotlin.base.extensions.isNull
import com.commonsense.android.kotlin.base.extensions.use
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.system.extensions.defaultDisplay
import com.commonsense.android.kotlin.system.logging.L
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * Created by Kasper Tvede½
 * Purpose:
 *
 */

typealias FpsWatcherCallback = (currentFps: Float,
                                droppedFrames: Float,
                                overdueTime: TimeUnit) -> Unit

class FpsWatcher(context: Context,
                 private val onFpsCallback: FpsWatcherCallback) {

    private val weakContext: WeakReference<Context> = context.weakReference()

    //this serves as a default, and only used if we cannot retrieve it though android;
    //since most displays are somewhere around 60 hz, we assume this is the default.
    private val defaultFps: Float = 60f

    private val runningAverage = RunningAverageFloatCapped(10)

    /**
     * try limit to fps, so we do not overload ui thread
     */
    private val backoffTimingInNs by lazy {
        TimeUnit.Milliseconds(
                TimeUnit.Seconds(1).toMilliseconds()
                        .milliSeconds / defaultFps.roundToInt())
                .toNanoSeconds()
                .nanoSeconds
    }

    private var lastAverage: Float = 0f

    private var lastTime: Long = 0

    /**
     * how many milliseconds there are in a second.
     */
    private val secondInMillisecond: Long = TimeUnit.Seconds(1)
            .toMilliseconds()
            .milliSeconds


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

        if (weakContext.get().isNull) {
            L.debug(FpsWatcher::class.java.simpleName, "detected context is null akk starter is dead, so we should close.")
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
            async(UI) {
                L.debug(FpsWatcher::class.java.simpleName, "fps is:$currentFps")
                onFpsCallback(average,
                        maxOf(expectedFps - currentFps, 0f),
                        TimeUnit.Milliseconds(missedTiming.toLong()))
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
            weakCallback.use { this(delta) }
        }
        lastTime = frameTimeNanos
        //reschedule our self.
        Choreographer.getInstance().postFrameCallback(this)
    }

}