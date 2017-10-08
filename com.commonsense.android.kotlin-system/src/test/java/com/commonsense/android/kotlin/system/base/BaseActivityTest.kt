package com.commonsense.android.kotlin.system.base

import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.failTest
import com.commonsense.android.kotlin.test.testCallbackWithSemaphore
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.robolectric.Robolectric

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
    fun launchInUiLifecycleEventsPausedResume() = testCallbackWithSemaphore(
            shouldAquire = true,
            errorMessage = "callback should be called after onresume after a pause") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        var counter = 0
        act.get().launchInUi("test", {
            if (counter == 0) {
                failTest("Should not get called when the pause or destroy have been called")
            } else {
                sem.release()
            }
        })
        runBlocking {
            Robolectric.flushBackgroundThreadScheduler()
            Robolectric.flushForegroundThreadScheduler()
            delay(50)
        }
        counter++
        act.resume()
    }


    @Test
    fun launchInBackgroundLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAquire = true,
            errorMessage = "callback should be called even when activity is paused.") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInBackground("test", {
            sem.release()
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


    @Test
    fun launchInBackgroundLifecycleEventsVisible() = testCallbackWithSemaphore(
            shouldAquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        act.get().launchInBackground("test", {
            sem.release()
        })
    }


}