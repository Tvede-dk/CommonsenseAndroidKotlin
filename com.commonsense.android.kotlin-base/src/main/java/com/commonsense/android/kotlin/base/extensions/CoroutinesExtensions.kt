package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunction
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
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

fun launch(context: CoroutineContext,
           start: CoroutineStart = CoroutineStart.DEFAULT,
           block: suspend () -> Unit): Job {
    return launch(context, start) {
        block()
    }
}

fun <T> asyncSimple(context: CoroutineContext,
                    start: CoroutineStart = CoroutineStart.DEFAULT,
                    block: suspend () -> T): Deferred<T> {
    return kotlinx.coroutines.experimental.async(context, start, {
        block()
    })
}

fun <T> asyncSimple(context: CoroutineContext,
                    block: suspend () -> T): Deferred<T> {
    return asyncSimple(context, CoroutineStart.DEFAULT, block)
}