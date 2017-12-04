package com.commonsense.android.kotlin.base

import kotlinx.coroutines.experimental.CoroutineScope

/**
 * Created by kasper on 21/07/2017.
 */
typealias EmptyFunction = () -> Unit
typealias EmptyFunctionResult<T> = () -> T
typealias AsyncEmptyFunction = suspend () -> Unit
typealias AsyncFunctionUnit<T> = suspend (T) -> Unit

/**
 * Function with 1 parameter that returns unit
 */
typealias FunctionUnit<E> = (E) -> Unit
typealias FunctionResult<I, O> = (I) -> O


typealias MapFunction<E, U> = (E) -> U

typealias SuspendFunctionUnit<E> = suspend (E) -> Unit

typealias FunctionBoolean<E> = (E) -> Boolean


typealias StringList = List<String>
/**
 *
 */
typealias AsyncCoroutineFunction = suspend CoroutineScope.() -> Unit