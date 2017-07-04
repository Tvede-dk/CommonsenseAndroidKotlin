package com.commonsense.android.kotlin.baseClasses

import android.support.v4.app.DialogFragment
import com.commonsense.android.kotlin.helperClasses.JobContainer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI

/**
 * created by Kasper Tvede on 29-09-2016.
 */

open class BaseFragment : DialogFragment() {

    private val localJobs by lazy {
        JobContainer()
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

    fun LaunchInBackground(group: String, action: suspend () -> Unit): Job {
        return localJobs.performAction(CommonPool, action, group)
    }


    fun LaunchInUi(group: String, action: suspend () -> Unit): Job {
        return localJobs.performAction(UI, action, group)
    }


    override fun onDestroy() {
        localJobs.cleanJobs()
        super.onDestroy()
    }

}