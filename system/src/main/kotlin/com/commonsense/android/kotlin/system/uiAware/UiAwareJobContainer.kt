package com.commonsense.android.kotlin.system.uiAware

import android.content.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.scheduling.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*

/**
 * Created by Kasper Tvede
 */
class UiAwareJobContainer : JobContainer() {

    /**
     * Redirect the UI event "onPostResume" here to
     */
    fun onPostResume() {
        executeQueueBackground("onPostResume")
    }

    /**
     * Redirect the UI event "onDestroy" here to
     */
    fun onDestroy() {
        cleanJobs()
    }

    /**
     * Redirect the UI event "onCreate" here to
     */
    fun onCreate() {
        cleanJobs()
    }

    /**
     * Launch a job in the UI when the ui is accessible / visible.
     * if the ui is not accessible/ visible then the job is queued up in the given group.
     * a job can change queue later on, to support starting it after the ui wakes up.
     * @param isUiVisible EmptyFunctionResult<Boolean>
     * @param group String
     * @param action AsyncFunctionUnit<Context>
     * @param context Context
     * @return Job
     */
    fun launchInUi(isUiVisible: EmptyFunctionResult<Boolean>,
                   group: String,
                   action: AsyncFunctionUnit<Context>,
                   context: Context): Job {
        val otherAction: AsyncEmptyFunction = {
            if (isUiVisible()) {
                action(context)
            } else {
                addToQueue(Dispatchers.Main, { action(context) }, "onPostResume")
            }
        }
        return performAction(Dispatchers.Main, otherAction, group)
    }
}