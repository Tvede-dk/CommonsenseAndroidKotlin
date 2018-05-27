package com.commonsense.android.kotlin.test

import android.app.Activity
import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.StyleRes
import org.junit.runner.RunWith

import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * Created by Kasper Tvede on 20-07-2017.
 * Base class of roboeletric tests, not requiring the manifest to be the set.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
abstract class BaseRoboElectricTest {
    /**
     * the context to use in roboelectric tests
     */
    val context: Context by lazy {
        RuntimeEnvironment.application
    }

    /**
     *
     */
    inline fun <reified T : Activity> createActivity(@StyleRes theme: Int = 0): T =
            createActivityController<T>(theme).create().get()

    /**
     * creates an activity controller to test lifecycle events
     */
    inline fun <reified T : Activity> createActivityController(@StyleRes theme: Int = 0): ActivityController<T> =
            Robolectric.buildActivity(T::class.java).apply {
                if (theme != 0) {
                    get().setTheme(theme)
                }
            }

    inline fun awaitAllTheading(crossinline condition: () -> Boolean,
                                @IntRange(from = 0) timeoutTime: Long,
                                TimeoutTimeUnit: TimeUnit,
                                s: String) {

        var remandingTimeInMs = TimeoutTimeUnit.toMillis(timeoutTime)
        while (!condition()) {
            val loopTime = measureTimeMillis {
                ShadowLooper.runUiThreadTasks()
                ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
                ShadowLooper.runMainLooperOneTask()
                ShadowLooper.runMainLooperToNextTask()
                Thread.sleep(10)
            }
            remandingTimeInMs -= loopTime
            if (remandingTimeInMs <= 0) {
                break
            }
        }
    }

}
