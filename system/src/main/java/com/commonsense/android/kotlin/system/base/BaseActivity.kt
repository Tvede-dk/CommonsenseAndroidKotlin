package com.commonsense.android.kotlin.system.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IntRange
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.system.PermissionsHandling
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.dataFlow.ReferenceCountingMap
import com.commonsense.android.kotlin.system.extensions.backPressIfHome
import com.commonsense.android.kotlin.system.logging.logWarning
import com.commonsense.android.kotlin.system.logging.tryAndLog
import com.commonsense.android.kotlin.system.uiAware.UiAwareJobContainer
import kotlinx.coroutines.experimental.CommonPool
import kotlin.reflect.KClass

/**
 * created by Kasper Tvede on 29-09-2016.
 */


open class BaseActivity : AppCompatActivity(), ActivityResultHelperContainer {

    /**
     * Handles permissions
     */
    val permissionHandler by lazy {
        PermissionsHandling()
    }

    //<editor-fold desc="on back press listener">
    private val onBackPressedListeners by lazy {
        mutableListOf<EmptyFunctionResult<Boolean>>()
    }
    /**
     * Manages the registration and especially un-registration of receivers.
     * since android does not perform a "forced" cleanup, this handles it.
     * however its functionality can be turned off by setting isEnabled to false.
     * its by default active
     */
    val receiverHandler by lazy {
        ActivityReceiversHelper()
    }

    /**
     *  Manges the keyboard, especially when switching screen ect.
     *
     * its by default active
     */
    val keyboardHandler by lazy {
        KeyboardHandlerHelper()
    }

    /**
     * The listener to get called before this activity handles the on back pressed event;
     * if it returns true then the event is not propagated further and the
     * activity does not call on back pressed on super
     */
    fun addOnBackPressedListener(listener: EmptyFunctionResult<Boolean>) {
        onBackPressedListeners.add(listener)
    }

    /**
     * Removes a listener, if registered.
     */
    fun removeOnBackPressedListener(listener: EmptyFunctionResult<Boolean>) {
        onBackPressedListeners.remove(listener)
    }
    //</editor-fold>

    private val localJobs by lazy {
        UiAwareJobContainer()
    }

    private val activityResultHelper by lazy {
        ActivityResultHelper({ logWarning(it) })
    }


    /**
     * a safe callback, that verifies the lifecycle, and also disallows multiple concurrent events of the same group.
     * Meant for updating the ui, or handling clicks'n events.
     */
    fun launchInUi(group: String, action: AsyncEmptyFunction) {
        localJobs.launchInUi({ isVisible }, group, action)
    }

    fun launchInBackground(group: String, action: AsyncEmptyFunction) {
        localJobs.performAction(CommonPool, action, group)
    }


    //<editor-fold desc="Lifecycle events">

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localJobs.onCreate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        receiverHandler.onDestroy(this)
        localJobs.onDestroy()
        activityResultHelper.clear()
        keyboardHandler.onDestroy(this)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            item.backPressIfHome(this) || super.onOptionsItemSelected(item)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHelper.handle(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        mIsPaused = false
    }

    override fun onPostResume() {
        super.onPostResume()
        localJobs.onPostResume()
    }

    override fun onPause() {
        super.onPause()
        mIsPaused = true
        keyboardHandler.onPause(this)
    }


    override fun onBackPressed() {
        //if any wants to handle the on back press of the listeners,
        // then we should "ignore it"
        if (onBackPressedListeners.any { it() }) {
            return
        }
        super.onBackPressed()
    }
    //</editor-fold>

    //<editor-fold desc="Add activity result listener">
    override fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOk(requestCode, receiver)
    }

    override fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback) {
        activityResultHelper.addForAllResults(requestCode, receiver)
    }

    override fun addActivityResultListenerOnlyOkAsync(requestCode: Int, receiver: AsyncActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOkAsync(requestCode, receiver)
    }

    override fun addActivityResultListenerAsync(requestCode: Int, receiver: AsyncActivityResultCallback) {
        activityResultHelper.addForAllResultsAsync(requestCode, receiver)
    }

    override fun removeActivityResultListener(@IntRange(from = 0) requestCode: Int) {
        activityResultHelper.remove(requestCode)
    }
    //</editor-fold>


    //<editor-fold desc="On paused ">
    val isPaused: Boolean
        get () = mIsPaused

    val isVisible: Boolean
        get() = !isPaused

    protected var mIsPaused: Boolean = false
    //</editor-fold>


    //<editor-fold desc="Register / unregister receivers">
    override fun registerReceiver(receiver: BroadcastReceiver?,
                                  filter: IntentFilter?): Intent? {
        receiverHandler.registerReceiver(receiver)
        return super.registerReceiver(receiver, filter)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?,
                                  filter: IntentFilter?,
                                  flags: Int): Intent? {
        receiverHandler.registerReceiver(receiver)
        return super.registerReceiver(receiver, filter, flags)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?,
                                  filter: IntentFilter?,
                                  broadcastPermission: String?,
                                  scheduler: Handler?): Intent? {
        receiverHandler.registerReceiver(receiver)
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?,
                                  filter: IntentFilter?,
                                  broadcastPermission: String?,
                                  scheduler: Handler?,
                                  flags: Int): Intent? {
        receiverHandler.registerReceiver(receiver)
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags)
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        tryAndLog(BaseActivity::class.java.simpleName) {
            receiverHandler.unregisterReceiver(receiver)
            super.unregisterReceiver(receiver)
        }
    }
    //</editor-fold>

    /**
     * internal such that the ActivityWithData can get these.
     */
    internal companion object {
        //the data intent index containing the index in the map.
        internal const val dataIntentIndex = "baseActivity-data-index"

        //shared storage.
        internal val dataReferenceMap = ReferenceCountingMap()
    }

}

//TODO should these be here ?

//there are 2 cases we solve, and 2 we do not solve;
/**
 * We solve the
 * [ctx -> Base ]
 * [base -> Base ]
 * but not the following
 * [ctx -> ctx]     } would require a generic non overriding solution which is impossible.
 * [base -> ctx]    } would mean that a generic activity or alike, would understand the data store,
 *                      which requires opening it up, but also for the user to copy +- the implementation
 *                         in BaseActivityData, which seem rather orthodox
 *
 *
 */

internal fun BaseActivity.cleanUpActivityWithDataMap(index: String) {
    BaseActivity.dataReferenceMap.decrementCounter(index)
}

fun <Input, T : BaseActivityData<Input>> Context.startActivityWithData(
        activity: KClass<T>,
        data: Input) {


}

fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    startActivityWithData(activity.java, data, requestCode, optOnResult)
}

fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    val intent = Intent(this, activity)
    val index = BaseActivity.dataReferenceMap.count.toString()
    BaseActivity.dataReferenceMap.addItem(data, index)
    intent.putExtra(BaseActivity.dataIntentIndex, index)
    startActivityForResultAsync(intent, null, requestCode, { resultCode, resultIntent ->
        BaseActivity.dataReferenceMap.decrementCounter(index)
        optOnResult?.invoke(resultCode, resultIntent)
    })
}


//TODO USE THE FOLLOWING PASSAGE FROM andorid dev doc:

/*
https://developer.android.com/guide/components/activities/activity-lifecycle.html#ondestroy

onDestroy()
Called before the activity is destroyed.
 This is the final call that the activity receives.
 The system either invokes this callback because the activity is finishing due to someone's calling finish(),
  or because the system is temporarily destroying the process containing the activity to save space.
   You can distinguish between these two scenarios with the isFinishing() method.
    The system may also call this method when an orientation change occurs, and then immediately call onCreate()
     to recreate the process (and the components that it contains) in the new orientation.

The onDestroy() callback releases all resources that have not yet been released by earlier callbacks such as onStop()

 */
