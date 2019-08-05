@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.base

import android.content.*
import android.os.*
import android.support.annotation.IntRange
import android.support.v7.app.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.system.permissions.*
import com.commonsense.android.kotlin.system.uiAware.*
import kotlinx.coroutines.*

/**
 * created by Kasper Tvede on 29-09-2016.
 */


open class BaseActivity : AppCompatActivity(), ActivityResultHelperContainer {

    /**
     * Handles permissions
     */
    internal val permissionHandler by lazy {
        PermissionsHandling()
    }

    //<editor-fold desc="on back press listener">
    /**
     * All on back listeners
     */
    private val onBackPressedListeners by lazy {
        mutableListOf<EmptyFunctionResult<Boolean>>()
    }
    /**
     * Manages the registration and espeically unregistration of receivers.
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
        this.onBackPressedListeners.add(listener)
    }

    /**
     * Removes a listener, if registered.
     */
    fun removeOnBackPressedListener(listener: EmptyFunctionResult<Boolean>) {
        onBackPressedListeners.remove(listener)
    }
    //</editor-fold>

    private val localJobs by lazy {
        UiAwareJobContainer(CoroutineScope(Dispatchers.Main))
    }

    private val activityResultHelper by lazy {
        ActivityResultHelper { logWarning(it) }//todo allow user code to overwrite this ? hmm
    }


    /**
     * a safe callback, that verifies the lifecycle, and also disallows multiple concurrent events of the same group.
     * Meant for updating the ui, or handling clicks'n events.
     * @param group String
     * @param action AsyncEmptyFunction
     */
    fun launchInUi(group: String, action: AsyncFunctionUnit<Context>): Job =
            localJobs.launchInUi({ isVisible }, group, action, this)


    /**
     * Launches the given action
     * @param group String the group name (So that no duplicates of this action can be schedualed at the same time)
     * @param action AsyncEmptyFunction the action to perform in the background
     */
    fun launchInBackground(group: String, action: AsyncEmptyFunction): Job =
            localJobs.performAction(Dispatchers.Default, action, group)


    //<editor-fold desc="Lifecycle events">

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localJobs.onCreate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionResult(this, requestCode, permissions, grantResults)
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
    /**
     * If this activity is paused
     */
    val isPaused: Boolean
        get () = mIsPaused

    /**
     * If this activity is visible, opposite of isPaused
     */
    val isVisible: Boolean
        get() = !isPaused

    protected var mIsPaused: Boolean = false
    //</editor-fold>


    //<editor-fold desc="Register / unregistter receivers">
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
        tryAndLog(BaseActivity::class) {
            receiverHandler.unregisterReceiver(receiver)
            super.unregisterReceiver(receiver)
        }
    }
    //</editor-fold>

    override fun toString(): String = toPrettyString()
    /**
     * Creates a pretty string representation of this internal state
     * @return String
     */
    open fun toPrettyString(): String {
        return "Base activity state: " + listOf(
                permissionHandler.toPrettyString(),
                onBackPressedListeners.map { "$it" }
                        .prettyStringContent("\ton back listeners",
                                "\tno on back listener"),
                receiverHandler.toPrettyString(),
                keyboardHandler.toPrettyString(),
                localJobs.toPrettyString(),
                activityResultHelper.toPrettyString()

        ).prettyStringContent()

    }

}
