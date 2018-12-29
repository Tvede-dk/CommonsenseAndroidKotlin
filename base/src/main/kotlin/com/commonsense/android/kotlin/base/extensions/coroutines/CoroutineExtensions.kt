package com.commonsense.android.kotlin.base.extensions.coroutines

import kotlinx.coroutines.*

//region async
fun <T> CoroutineScope.asyncIO(start: CoroutineStart = CoroutineStart.DEFAULT,
                               block: suspend CoroutineScope.() -> T): Deferred<T> =
        async(Dispatchers.IO, start, block)

fun <T> CoroutineScope.asyncDefault(start: CoroutineStart = CoroutineStart.DEFAULT,
                                    block: suspend CoroutineScope.() -> T): Deferred<T> =
        async(Dispatchers.Default, start, block)


fun <T> CoroutineScope.asyncMain(start: CoroutineStart = CoroutineStart.DEFAULT,
                                 block: suspend CoroutineScope.() -> T): Deferred<T> =
        async(Dispatchers.Main, start, block)
//endregion

//region launch
fun CoroutineScope.launchIO(start: CoroutineStart = CoroutineStart.DEFAULT,
                            block: suspend CoroutineScope.() -> Unit): Job =
        launch(Dispatchers.IO, start, block)

fun CoroutineScope.launchDefault(start: CoroutineStart = CoroutineStart.DEFAULT,
                                 block: suspend CoroutineScope.() -> Unit): Job =
        launch(Dispatchers.Default, start, block)

fun CoroutineScope.launchMain(start: CoroutineStart = CoroutineStart.DEFAULT,
                              block: suspend CoroutineScope.() -> Unit): Job =
        launch(Dispatchers.Main, start, block)
//endregion
