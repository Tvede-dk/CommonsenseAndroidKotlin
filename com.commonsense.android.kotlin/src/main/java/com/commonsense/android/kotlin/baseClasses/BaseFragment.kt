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
    fun addLocalJob(job: Job) {
        localJobs.addJob(job)
    }

    fun LaunchInBackground(action: suspend () -> Unit): Job {
        return localJobs.performAction(CommonPool, action)
    }


    fun LaunchInUi(action: suspend () -> Unit): Job {
        return localJobs.performAction(UI, action)
    }


    override fun onDestroy() {
        localJobs.cleanJobs()
        super.onDestroy()
    }

}