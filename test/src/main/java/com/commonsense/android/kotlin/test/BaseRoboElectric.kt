package com.commonsense.android.kotlin.test

import android.app.Activity
import android.content.Context
import android.support.annotation.StyleRes
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

/**
 * Created by Kasper Tvede on 20-07-2017.
 * Base class of roboeletric tests, not requiring the manifest to be the set.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
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


}
