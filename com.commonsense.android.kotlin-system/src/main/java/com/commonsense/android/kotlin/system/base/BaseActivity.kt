package com.commonsense.android.kotlin.system.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.IntRange
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.system.PermissionsHandling
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.dataFlow.ReferenceCountingMap
import com.commonsense.android.kotlin.system.extensions.backPressIfHome
import com.commonsense.android.kotlin.system.extensions.transactionCommit
import com.commonsense.android.kotlin.system.extensions.transactionCommitNow
import com.commonsense.android.kotlin.system.logging.logWarning
import com.commonsense.android.kotlin.system.uiAware.UiAwareJobContainer
import kotlinx.coroutines.experimental.CommonPool
import kotlin.reflect.KClass

/**
 * created by Kasper Tvede on 29-09-2016.
 */


open class BaseActivity : AppCompatActivity(), ActivityResultHelperContainer {

    val permissionHandler by lazy {
        PermissionsHandling()
    }

    //<editor-fold desc="on back press listener">
    private val onBackPressedListeners by lazy {
        mutableListOf<EmptyFunctionResult<Boolean>>()
    }

    /**
     * The listener to get called before this activity handles the on back pressed event;
     * if it returns true then the event is not propagated further and the
     * activity does not call on back pressed on super
     */
    fun addOnbackPressedListener(listener: EmptyFunctionResult<Boolean>) {
        onBackPressedListeners.add(listener)
    }

    /**
     * Removes a listener, if registered.
     */
    fun removeOnbackPressedListener(listener: EmptyFunctionResult<Boolean>) {
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
        localJobs.onDestory()
        activityResultHelper.clear()
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


    /**
     * Protected such that the ActivityWithData can get these.
     */
    internal companion object {
        internal val dataIntentIndex = "baseActivity-data-index"
        internal val dataReferenceMap = ReferenceCountingMap()
    }

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

@AnyThread
fun Activity.safeFinish() = runOnUiThread(this::finish)


//<editor-fold desc="push / replace fragment">
@UiThread
fun FragmentActivity.replaceFragment(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.transactionCommitNow {
        replace(container, fragment)
    }
}

@UiThread
fun FragmentActivity.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.transactionCommit {
        replace(container, fragment)
        addToBackStack(fragment.id.toString())
    }
}
//</editor-fold>

