package com.commonsense.android.kotlin.test

import android.app.Activity
import android.content.Context
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Created by Kasper Tvede on 20-07-2017.
 */

@RunWith(RobolectricTestRunner::class)
abstract class BaseRoboElectricTest {
    val context: Context by lazy {
        RuntimeEnvironment.application
    }

    inline fun <reified T : Activity> createActivity(): T =
            Robolectric.buildActivity(T::class.java).create().get()
}
