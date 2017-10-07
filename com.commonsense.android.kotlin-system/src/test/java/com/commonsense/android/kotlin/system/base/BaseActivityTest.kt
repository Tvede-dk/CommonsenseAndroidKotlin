package com.commonsense.android.kotlin.system.base

import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.failTest
import com.commonsense.android.kotlin.test.testCallbackWithSemaphore
import org.junit.Test

/**
 * Created by Kasper Tvede on 07-10-2017.
 */
class BaseActivityTest : BaseRoboElectricTest() {
    @Test
    fun launchInUiLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAquire = false,
            errorMessage = "callback should not be called") { _ ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInUi("test", {
            failTest("Should not get called when the pause or destroy have been called")
        })
    }

    @Test
    fun launchInUiLifecycleEventsVisible() = testCallbackWithSemaphore(
            shouldAquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        act.get().launchInUi("test", {
            sem.release()
        })
    }


}