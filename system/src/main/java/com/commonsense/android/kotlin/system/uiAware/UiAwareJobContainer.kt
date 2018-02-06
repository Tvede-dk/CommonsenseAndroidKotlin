package com.commonsense.android.kotlin.system.uiAware

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.scheduling.JobContainer
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI

/**
 * Created by Kasper Tvede on 08-10-2017.
 */
class UiAwareJobContainer : JobContainer() {

    fun onPostResume() {
        executeQueue("onPostResume")
    }

    fun onDestroy() {
        cleanJobs()

    }

    fun onCreate() {
        cleanJobs()
    }


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