package com.commonsense.android.kotlin

import android.app.Activity
import com.commonsense.kotlin.BuildConfig
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


/**
 * Created by Kasper Tvede on 27-05-2017.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
abstract class BaseRoboElectricTest {
    val context by lazy {
        RuntimeEnvironment.application
    }

    inline fun <reified T : Activity> createActivity(): T {
        return Robolectric.buildActivity(T::class.java).create().get()
    }
}
