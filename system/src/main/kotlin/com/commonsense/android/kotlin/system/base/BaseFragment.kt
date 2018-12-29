package com.commonsense.android.kotlin.system.base

import android.app.*
import android.content.*
import android.os.*
import android.support.annotation.IntRange
import android.support.v4.app.DialogFragment
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.system.permissions.*
import com.commonsense.android.kotlin.system.uiAware.*
import kotlinx.coroutines.*


/**
 * Base class for smarter fragments.
 * it handles various things, such as scheduling on the ui thread when visible (as opposed to the regular scheduling that just runs on the ui tread)
 * it also handles dismissing the keyboard between screen changes.
 * Some of the features depend on the activity being a BaseActivity
 *
 */
open class BaseFragment : DialogFragment(), ActivityResultHelperContainer {

    /**
     * Gateway for permission handling.
     * This requires the hosting activity to be a BaseActivity
     * if the parent is not a BaseActivity or not there an error will be logged.
     */
    internal val permissionHandler: PermissionsHandling?
        get() {
            if (baseActivity == null) {
                logError("The activity is either not a base activity or its not there;" +
                        " the permission handling only works with BaseActivity")
            }
            return baseActivity?.permissionHandler
        }
    /**
     * returns the activity as the base activity; if the activity is not a base activity then null is returned regardless.
     */
    val baseActivity: BaseActivity?
        get() = activity as? BaseActivity

    /**
     * a safe way to retrieve the activity since its not annotated nullable.
     */
    val safeActivity: Activity?
        get() = activity


    /**
     *
     */
    private val localJobs by lazy {
        UiAwareJobContainer()
    }

    /**
     * The keyboard handler, responsible for handling keyboard interactions.
     */
    val keyboardHandler by lazy {
        KeyboardHandlerHelper()
    }


    private val activityResultHelper by lazy {
        ActivityResultHelper { logWarning(it) }
    }

    /**
     * reschedules on the ui thread to be dismissed, and removes all local jobs (cleanup)
     */
    override fun dismiss() {
        localJobs.cleanJobs()
        launchInUi("dismiss") {
            super.dismiss()
        }
    }

    /**
     * in case you have something else than a regular "launch" / async style, then you can still
     * add the jobs manually. eg some api composing of async / launch api'
     * @param group String
     * @param job Job
     * @return Unit
     */
    fun addLocalJob(group: String, job: Job): Unit =
            localJobs.addJob(job, group)

    /**
     *
     * @param group String
     * @param action suspend () -> Unit
     * @return Job
     */
    fun launchInBackground(group: String, action: AsyncEmptyFunction): Job =
            localJobs.performAction(Dispatchers.Default, action, group)


    /**
     *
     * @param group String
     * @param action suspend () -> Unit
     * @return Job? null if the context is unavailable.
     */
    fun launchInUi(group: String, action: AsyncFunctionUnit<Context>): Job? {
        val context = context ?: return null
        return localJobs.launchInUi({ isAdded && !this.isHidden && isResumed }, group, action, context)
    }

    override fun onResume() {
        super.onResume()
        localJobs.onPostResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localJobs.onCreate()
    }

    override fun onDestroy() {
        localJobs.onDestroy()
        activityResultHelper.clear()
        super.onDestroy()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(@IntRange(from = 0) requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHelper.handle(requestCode, resultCode, data)
    }

    //<editor-fold desc="Add activity result listener">
    override fun addActivityResultListenerOnlyOk(
            @IntRange(from = 0) requestCode: Int,
            receiver: ActivityResultCallbackOk) =
            activityResultHelper.addForOnlyOk(requestCode, receiver)

    override fun addActivityResultListener(
            @IntRange(from = 0) requestCode: Int,
            receiver: ActivityResultCallback) =
            activityResultHelper.addForAllResults(requestCode, receiver)

    override fun removeActivityResultListener(
            @IntRange(from = 0) requestCode: Int) =
            activityResultHelper.remove(requestCode)

    override fun addActivityResultListenerOnlyOkAsync(
            @IntRange(from = 0) requestCode: Int,
            receiver: AsyncActivityResultCallbackOk) =
            activityResultHelper.addForOnlyOkAsync(requestCode, receiver)

    override fun addActivityResultListenerAsync(
            @IntRange(from = 0) requestCode: Int,
            receiver: AsyncActivityResultCallback) =
            activityResultHelper.addForAllResultsAsync(requestCode, receiver)
    //</editor-fold>
}