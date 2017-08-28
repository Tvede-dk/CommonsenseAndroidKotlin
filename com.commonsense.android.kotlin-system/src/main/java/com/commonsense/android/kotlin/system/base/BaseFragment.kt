package com.commonsense.android.kotlin.system.base

import android.content.Intent
import android.support.annotation.IntRange
import android.support.v4.app.DialogFragment
import android.view.MenuItem
import com.commonsense.android.kotlin.base.scheduling.JobContainer
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.onBackPressed
import com.commonsense.android.kotlin.system.logging.logWarning
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI

/**
 * created by Kasper Tvede on 29-09-2016.
 */

open class BaseFragment : DialogFragment(), ActivityResultHelperContainer {

    protected val activityAsBase
        get() = activity as BaseActivity


    private val localJobs by lazy {
        JobContainer()
    }

    private val activityResultHelper by lazy {
        ActivityResultHelper({ logWarning(it) })
    }

    override fun dismiss() {
        localJobs.cleanJobs()
        super.dismiss()
    }

    /**
     * in case you have something else than a regular "launch" / async style, then you can still
     * add the jobs manually. eg some api composing of async / launch api'
     */
    fun addLocalJob(group: String, job: Job) {
        localJobs.addJob(job, group)
    }

    fun LaunchInBackground(group: String, action: suspend () -> Unit): Job =
            localJobs.performAction(CommonPool, action, group)


    fun LaunchInUi(group: String, action: suspend () -> Unit): Job =
            localJobs.performAction(UI, action, group)


    override fun onDestroy() {
        localJobs.cleanJobs()
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