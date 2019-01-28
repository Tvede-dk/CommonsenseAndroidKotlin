package com.commonsense.android.kotlin.test

import android.app.*
import android.content.*
import android.support.annotation.*
import android.support.annotation.IntRange
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.android.controller.*
import org.robolectric.annotation.*
import org.robolectric.shadows.*
import java.util.concurrent.*
import kotlin.system.*

/**
 * Created by Kasper Tvede on 20-07-2017.
 * Base class of roboeletric tests, not requiring the manifest to be the set.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [16, 21, 27])
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
                                message: String = "awaiting condition failed") {

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
                failTest(message)
                break
            }
        }
    }

}
