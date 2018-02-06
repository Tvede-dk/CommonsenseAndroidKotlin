package com.commonsense.android.kotlin.system.base

import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.system.logging.tryAndLog
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.failTest
import com.commonsense.android.kotlin.test.testCallbackWithSemaphore
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

/**
 * Created by Kasper Tvede on 07-10-2017.
 */
@Config(sdk = [21])
class BaseActivityTest : BaseRoboElectricTest() {
    @Test
    fun launchInUiLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAcquire = false,
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
            shouldAcquire = true,
            errorMessage = "callback should be called after onResume after a pause") { sem ->
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
    fun launchInUiLifecycleEventsPausedDestroy() = testCallbackWithSemaphore(
            shouldAcquire = false,
            errorMessage = "callback should be called after onResume after a pause") {
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInUi("test", {
            failTest("Should not get called when the pause or destroy have been called")
        })
        runBlocking {
            Robolectric.flushBackgroundThreadScheduler()
            Robolectric.flushForegroundThreadScheduler()
            delay(50)
        }
        act.destroy()

        (tryAndLog("should throw due to ") {
            act.postResume()
            false
        } ?: true).assert(true, "should throw as the activity is dead. ")

    }


    @Test
    fun launchInBackgroundLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAcquire = true,
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
            shouldAcquire = true,
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
            shouldAcquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        act.get().launchInBackground("test", {
            sem.release()
        })

    }

    @Test
    fun addOnBackPressedListeners() = testCallbackWithSemaphore(
            shouldAcquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        val listener = {
            sem.release()
            true
        }
        act.get().apply {
            addOnBackPressedListener(listener)
        }
        act.get().onBackPressed()
    }

    @Test
    fun removeOnBackPressedListeners() = testCallbackWithSemaphore(
            shouldAcquire = false,
            errorMessage = "callback should NOT be called") {
        val act = createActivityController<BaseActivity>(R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        val listener = {
            failTest("Should not be called once removed.")
            true
        }
        act.get().apply {
            addOnBackPressedListener(listener)
            removeOnBackPressedListener(listener)
        }
        act.get().onBackPressed()
    }

    /* @Test
     fun onBackPressedSuperNotCalledDueToListener() {
         //TODO make me
         //listener for the supers on back pressed and tell if it gets called. in all senarious (no listeners,
            , only no listeners, only yes listeners, mixture).
            

     }*/


}