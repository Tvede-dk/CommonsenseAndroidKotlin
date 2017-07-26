package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunction
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by Kasper Tvede on 22-07-2017.
 */
/**
 * when the job is completed, performs the given action on the given context.
 */
inline fun Job.launchOnCompleted(context: CoroutineContext, crossinline action: EmptyFunction) {
    invokeOnCompletion {
        launch(context) {
            action()
        }
    }
}

/**
 * when the job is completed, performs the given action on the given context.
 * works with suspendable functions.
 */
fun Job.launchOnCompletedAsync(context: CoroutineContext, action: AsyncEmptyFunction) {
    invokeOnCompletion {
        launch(context) {
            action()
        }
    }
}