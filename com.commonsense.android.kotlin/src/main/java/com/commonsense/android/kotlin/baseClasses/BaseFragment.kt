package com.commonsense.android.kotlin.baseClasses

import android.support.v4.app.DialogFragment
import com.commonsense.android.kotlin.helperClasses.JobContainer
import kotlinx.coroutines.experimental.CommonPool
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


    fun LaunchInBackground(action: suspend () -> Unit) {
        localJobs.performAction(CommonPool, action)
    }


    fun LaunchInUi(action: suspend () -> Unit) {
        localJobs.performAction(UI, action)
    }


    override fun onDestroy() {
        localJobs.cleanJobs()
        super.onDestroy()
    }

}