package com.commonsense.android.kotlin.system.uiAware

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.base.scheduling.JobContainer
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI

/**
 * Created by Kasper Tvede on 08-10-2017.
 */
class UiAwareJobContainer : JobContainer() {

    /**
     * Redirect the UI event "onPostResume" here to
     */
    fun onPostResume(): Unit {
        executeQueueBackground("onPostResume")
    }

    /**
     * Redirect the UI event "onDestroy" here to
     */
    fun onDestory(): Unit {
        cleanJobs()
    }

    /**
     * Redirect the UI event "onCreate" here to
     */
    fun onCreate(): Unit {
        cleanJobs()
    }

    /**
     * Redirect the UI event "onStop" here to, with the additional information "isFinalizing".
     */
    fun onStop(isFinializing: Boolean): Unit {
        isFinializing.ifTrue(::cleanJobs)
    }

    /**
     * Launch a job in the UI when the ui is accessible / visible.
     * if the ui is not accessible/ visible then the job is queued up in the given group.
     * a job can change queue later on, to support starting it after the ui wakes up.
     */
    fun launchInUi(isUiVisible: () -> Boolean, group: String, action: AsyncEmptyFunction): Job {
        val otherAction: AsyncEmptyFunction = {
            if (isUiVisible()) {
                action()
            } else {
                addToQueue(UI, action, "onPostResume")
            }
        }
        return performAction(UI, otherAction, group)
    }
}