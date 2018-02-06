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


/**
For library functions
 */
        /**
         * Function with 1 input and potential output
         */
typealias Function1<I1, O> = (I1) -> O

        /**
         * Function with 2 inputs and potential output
         */
typealias Function2<I1, I2, O> = (I1, I2) -> O

        /**
         * Function with 3 inputs and potential output
         */
typealias Function3<I1, I2, I3, O> = (I1, I2, I3) -> O

        /**
         * Function with 4 inputs and potential output
         */
typealias Function4<I1, I2, I3, O> = (I1, I2, I3) -> O

        /**
         * Function with 5 inputs and potential output
         */
typealias Function5<I1, I2, I3, O> = (I1, I2, I3) -> O

        /**
         * Function with 6 inputs and potential output
         */
typealias Function6<I1, I2, I3, O> = (I1, I2, I3) -> O
