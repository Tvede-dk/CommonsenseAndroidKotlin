package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.AsyncFunctionUnit
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
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

fun <T> asyncSimple(context: CoroutineContext = CommonPool,
                    start: CoroutineStart = CoroutineStart.DEFAULT,
                    block: suspend () -> T): Deferred<T> {
    return kotlinx.coroutines.experimental.async(context, start, null, {
        block()
    })
}

fun <T> asyncSimple(context: CoroutineContext = CommonPool,
                    block: suspend () -> T): Deferred<T> {
    return asyncSimple(context, CoroutineStart.DEFAULT, block)
}

/**
 * Awaits a list of deferred computations.
 * and returns the resulting list.
 * @return the computed results awaited.
 */
suspend fun <T> List<Deferred<T>>.await(): List<T> {
    return this.map { it.await() }
}


suspend fun List<Job>.awaitAll() {
    this.forEach { job: Job -> job.join() }
}

suspend inline fun <E> Channel<E>.forEach(crossinline function: FunctionUnit<E>) {
    for (item in this) {
        function(item)
    }
}

suspend fun <E> Channel<E>.forEachAsync(function: AsyncFunctionUnit<E>) {
    for (item in this) {
        function(item)
    }
}