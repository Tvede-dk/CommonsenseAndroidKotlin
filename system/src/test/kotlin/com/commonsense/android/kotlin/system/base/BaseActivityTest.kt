package com.commonsense.android.kotlin.system.base

import android.os.Looper.*
import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.*
import org.junit.*
import org.robolectric.*
import org.robolectric.Shadows.*
import org.robolectric.annotation.*
import java.util.concurrent.*

/**
 * Created by Kasper Tvede on 07-10-2017.
 */
@Config(sdk = [21])
class BaseActivityTest : BaseRoboElectricTest() {

    @Throws(InterruptedException::class)
    @Test
    fun launchInUiLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAcquire = false,
            errorMessage = "callback should not be called") { _ ->
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInUi("test") {
            failTest("Should not get called when the pause or destroy have been called")
        }
    }

    @Throws(InterruptedException::class)
    @Test
    fun launchInUiLifecycleEventsPausedResume() {
        val sem = Semaphore(0)
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        var counter = 0
        act.get().launchInUi("test") {
            if (counter == 0) {
                failTest("Should not get called when the pause or destroy have been called")
            } else {
                sem.release()
            }
        }
        counter += 1
        act.visible()
        act.resume()
        act.postResume()
        awaitAllTheading({ sem.tryAcquire() },
                1,
                TimeUnit.SECONDS,
                "callback should be called after onresume after a pause")
    }

    @Throws(InterruptedException::class)
    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun launchInUiLifecycleEventsPausedDestory() = testCallbackWithSemaphore(
            shouldAcquire = false,
            errorMessage = "callback should be called after onresume after a pause") {
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInUi("test") {
            failTest("Should not get called when the pause or destroy have been called")
        }
        runBlocking {
            shadowOf(getMainLooper()).idle()
            delay(50)
        }
        act.destroy()

        (tryAndLog("should throw due to ") {
            act.postResume()
            false
        } ?: true).assert(true, "should throw as the activity is dead. ")

    }


    @Throws(InterruptedException::class)
    @Test
    fun launchInBackgroundLifecycleEventsPaused() = testCallbackWithSemaphore(
            shouldAcquire = true,
            errorMessage = "callback should be called even when activity is paused.") { sem ->
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            pause()
        }
        act.get().launchInBackground("test") {
            sem.release()
        }
    }

    @Throws(InterruptedException::class)
    @Test
    fun launchInUiLifecycleEventsVisible() = testCallbackWithSemaphore(
            shouldAcquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        act.get().launchInUi("test") {
            sem.release()
        }
        shadowOf(getMainLooper()).idle()

    }


    @Throws(InterruptedException::class)
    @Test
    fun launchInBackgroundLifecycleEventsVisible() = testCallbackWithSemaphore(
            shouldAcquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        act.get().launchInBackground("test") {
            sem.release()
        }

    }

    @Throws(InterruptedException::class)
    @Test
    fun addOnBackPressedListeners() = testCallbackWithSemaphore(
            shouldAcquire = true,
            errorMessage = "callback should be called") { sem ->
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        val listerner = {
            sem.release()
            true
        }
        act.get().apply {
            addOnBackPressedListener(listerner)
        }
        act.get().onBackPressed()
    }

    @Throws(InterruptedException::class)
    @Test
    fun removeOnBackPressedListeners() = testCallbackWithSemaphore(
            shouldAcquire = false,
            errorMessage = "callback should NOT be called") {
        val act = createActivityController<BaseActivity>(androidx.appcompat.R.style.Theme_AppCompat).apply {
            setup()
            visible()
        }
        val listerner = {
            failTest("Should not be called once removed.")
            true
        }
        act.get().apply {
            addOnBackPressedListener(listerner)
            removeOnBackPressedListener(listerner)
        }
        act.get().onBackPressed()
    }

    /* @Test
     fun onBackPressedSuperNotCalledDueToListener() {
         //TODO make me
         //listener for the supers on back pressed and tell if it gets called. in all senarious (no listeners,
            , only no listeners, only yes listeners, mixture).
            

     }*/
    @Ignore
    @Test
    fun `getPermissionHandler$system_debug`() {
    }

    @Ignore
    @Test
    fun getReceiverHandler() {
    }

    @Ignore
    @Test
    fun getKeyboardHandler() {
    }

    @Ignore
    @Test
    fun addOnBackPressedListener() {
    }

    @Ignore
    @Test
    fun removeOnBackPressedListener() {
    }

    @Ignore
    @Test
    fun launchInUi() {
    }

    @Ignore
    @Test
    fun launchInBackground() {
    }

    @Ignore
    @Test
    fun onCreate() {
    }

    @Ignore
    @Test
    fun onRequestPermissionsResult() {
    }

    @Ignore
    @Test
    fun onDestroy() {
    }

    @Ignore
    @Test
    fun onOptionsItemSelected() {
    }

    @Ignore
    @Test
    fun onActivityResult() {
    }

    @Ignore
    @Test
    fun onResume() {
    }

    @Ignore

    @Test
    fun onPostResume() {
    }

    @Ignore
    @Test
    fun onPause() {
    }

    @Ignore
    @Test
    fun onBackPressed() {
    }

    @Ignore
    @Test
    fun addActivityResultListenerOnlyOk() {
    }

    @Ignore
    @Test
    fun addActivityResultListener() {
    }

    @Ignore
    @Test
    fun addActivityResultListenerOnlyOkAsync() {
    }

    @Ignore
    @Test
    fun addActivityResultListenerAsync() {
    }

    @Ignore
    @Test
    fun removeActivityResultListener() {
    }

    @Ignore
    @Test
    fun isPaused() {
    }

    @Ignore
    @Test
    fun isVisible() {
    }

    @Ignore
    @Test
    fun getMIsPaused() {
    }

    @Ignore
    @Test
    fun setMIsPaused() {
    }

    @Ignore
    @Test
    fun registerReceiver() {
    }

    @Ignore
    @Test
    fun registerReceiver1() {
    }

    @Ignore
    @Test
    fun registerReceiver2() {
    }

    @Ignore
    @Test
    fun registerReceiver3() {
    }

    @Ignore
    @Test
    fun unregisterReceiver() {
    }

    @Ignore
    @Test
    fun toPrettyString() {
    }
}
