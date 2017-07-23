package com.commonsense.android.kotlin.base

import kotlinx.coroutines.experimental.CoroutineScope

/**
 * Created by kasper on 21/07/2017.
 */
typealias EmptyFunction = () -> Unit

typealias AsyncEmptyFunction = suspend () -> Unit

/**
 * Function with 1 parameter that returns unit
 */
typealias FunctionUnit<E> = (E) -> Unit

typealias FunctionBoolean<E> = (E) -> Boolean


/**
 *
 */
typealias AsyncCoroutineFunction = suspend CoroutineScope.() -> Unit