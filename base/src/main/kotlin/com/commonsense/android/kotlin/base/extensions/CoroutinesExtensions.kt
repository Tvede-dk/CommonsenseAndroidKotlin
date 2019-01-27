@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlin.coroutines.*

/**
 * Created by Kasper Tvede on 22-07-2017.
 */
/**
 * when the job is completed, performs the given action on the given context.
 */
fun Job.launchOnCompleted(context: CoroutineContext,
                          action: EmptyFunction) {
    invokeOnCompletion {

        GlobalScope.launch(context, CoroutineStart.DEFAULT, {
            action()
        })
    }
}

/**
 * when the job is completed, performs the given action on the given context.
 * works with suspendable functions.
 */
fun Job.launchOnCompletedAsync(context: CoroutineContext,
                               action: AsyncEmptyFunction) {
    invokeOnCompletion {
        GlobalScope.launch(context, CoroutineStart.DEFAULT, {
            action()
        })
    }
}

/**
 * Like the original launch, except without the coroutineScope 'this parameter
 */
fun launchBlock(context: CoroutineContext,
                start: CoroutineStart = CoroutineStart.DEFAULT,
                block: AsyncEmptyFunction): Job {
    return GlobalScope.launch(context, start, {
        block()
    })
}

/**
 * Like the original async, except without the coroutineScope 'this parameter
 */
fun <T> asyncSimple(context: CoroutineContext = Dispatchers.Default,
                    start: CoroutineStart = CoroutineStart.DEFAULT,
                    block: suspend () -> T): Deferred<T> {
    return GlobalScope.async(context,
            start,
            { block() })
}

/**
 * Like the original async, except without the coroutineScope 'this parameter
 */
fun <T> asyncSimple(
        context: CoroutineContext = Dispatchers.Default,
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
//
///**
// * Waits for all the given jobs to finish.
// */
//suspend fun List<Job>.awaitAll() {
//    this.forEach { it.join() }
//}

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