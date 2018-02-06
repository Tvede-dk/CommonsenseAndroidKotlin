package com.commonsense.android.kotlin.system.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntRange
import android.support.v4.app.DialogFragment
import android.view.MenuItem
import com.commonsense.android.kotlin.system.PermissionsHandling
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.onBackPressed
import com.commonsense.android.kotlin.system.logging.logError
import com.commonsense.android.kotlin.system.logging.logWarning
import com.commonsense.android.kotlin.system.uiAware.UiAwareJobContainer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job

/**
 * created by Kasper Tvede on 29-09-2016.
 */

open class BaseFragment : DialogFragment(), ActivityResultHelperContainer {

    /**
     * Gateway for permission handling.
     * This requires the hosting activity to be a BaseActivity
     * if the parent is not a BaseActivity or not there an error will be logged.
     */
    val permissionHandler: PermissionsHandling?
        get() {
            if (baseActivity == null) {
                logError("The activity is either not a base activity or its not there;" +
                        " the permission handling only works with BaseActivity")
            }
            return baseActivity?.permissionHandler
        }
    /**
     *
     */
    val baseActivity: BaseActivity?
        get() = activity as? BaseActivity

    /**
     *
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
     *
     */
    val keyboardHandler by lazy {
        KeyboardHandlerHelper()
    }

    private val activityResultHelper by lazy {
        ActivityResultHelper({ logWarning(it) })
    }

    override fun dismiss() {
        localJobs.cleanJobs()
        launchInUi("dismiss") {
            super.dismiss()
        }
    }

    /**
     * in case you have something else than a regular "launch" / async style, then you can still
     * add the jobs manually. eg some api composing of async / launch api'
     */
    fun addLocalJob(group: String, job: Job) {
        localJobs.addJob(job, group)
    }

    fun launchInBackground(group: String, action: suspend () -> Unit): Job =
            localJobs.performAction(CommonPool, action, group)


    fun launchInUi(group: String, action: suspend () -> Unit): Job =
            localJobs.launchInUi({ isAdded && !this.isHidden && isResumed }, group, action)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHelper.handle(requestCode, resultCode, data)
    }

    //<editor-fold desc="Add activity result listener">
    override fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOk(requestCode, receiver)
    }

    override fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback) {
        activityResultHelper.addForAllResults(requestCode, receiver)
    }

    override fun removeActivityResultListener(@IntRange(from = 0) requestCode: Int) {
        activityResultHelper.remove(requestCode)
    }

    override fun addActivityResultListenerOnlyOkAsync(requestCode: Int, receiver: AsyncActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOkAsync(requestCode, receiver)
    }

    override fun addActivityResultListenerAsync(requestCode: Int, receiver: AsyncActivityResultCallback) {
        activityResultHelper.addForAllResultsAsync(requestCode, receiver)
    }
    //</editor-fold>
}