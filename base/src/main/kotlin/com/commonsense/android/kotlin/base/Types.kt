@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base

import kotlinx.coroutines.*

/**
 * Created by kasper on 21/07/2017.
 */
typealias EmptyFunction = () -> Unit

typealias EmptyReceiver<T> = T.() -> Unit
typealias EmptyFunctionResult<T> = () -> T
typealias AsyncEmptyFunction = suspend () -> Unit
typealias AsyncFunctionUnit<T> = suspend (T) -> Unit

typealias AsyncEmptyFunctionResult<T> = suspend () -> T

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

/**
 * Function with 1 input and potential output
 *
 */
typealias AsyncFunction1<I1, O> = suspend (I1) -> O