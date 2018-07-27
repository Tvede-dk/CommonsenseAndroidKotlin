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
fun Job.launchOnCompleted(context: CoroutineContext,
                          action: EmptyFunction) {
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
fun Job.launchOnCompletedAsync(context: CoroutineContext,
                               action: AsyncEmptyFunction) {
    invokeOnCompletion {
        launch(context) {
            action()
        }
    }
}

/**
 * Like the original launch, except without the coroutineScope 'this parameter
 */
fun launchBlock(context: CoroutineContext,
                start: CoroutineStart = CoroutineStart.DEFAULT,
                block: AsyncEmptyFunction): Job {
    return kotlinx.coroutines.experimental.launch(context, start) {
        block()
    }
}

/**
 * Like the original async, except without the coroutineScope 'this parameter
 */
fun <T> asyncSimple(context: CoroutineContext = CommonPool,
                    start: CoroutineStart = CoroutineStart.DEFAULT,
                    block: suspend () -> T): Deferred<T> {
    return kotlinx.coroutines.experimental.async(
            context,
            start,
            null,
            null,
            { block() })
}

/**
 * Like the original async, except without the coroutineScope 'this parameter
 */
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

/**
 * Waits for all the given jobs to finish.
 */
suspend fun List<Job>.awaitAll() {
    this.forEach { it.join() }
}

suspend fun <E> Channel<E>.forEach(function: FunctionUnit<E>) {
    for (item in this) {
        function(item)
    }
}

suspend fun <E> Channel<E>.forEachAsync(function: AsyncFunctionUnit<E>) {
    for (item in this) {
        function(item)
    }
}