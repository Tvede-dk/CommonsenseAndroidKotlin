package com.commonsense.android.kotlin.system.logging

import com.commonsense.android.kotlin.base.AsyncEmptyFunctionResult
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import kotlin.reflect.KClass

typealias loggerFunction = (tag: String, message: String, stackTrace: Throwable?) -> Any

/**
 * Created by Kasper Tvede on 18-07-2017.
 */

//reason for splitting: compiler bugs; apprently referencing a static method in a default argument causes a compiler issue.
// thx jetbrains..


inline fun <T> tryAndLog(classType: KClass<*>,
                         message: String = "",
                         crossinline action: EmptyFunctionResult<T>): T? =
        tryAndLog(classType.java.simpleName, message, L::error, action)


inline fun <T> tryAndLog(classType: KClass<*>,
                         message: String = "",
                         logger: loggerFunction,
                         crossinline action: EmptyFunctionResult<T>): T? =
        tryAndLog(classType.java.simpleName, message, logger, action)


inline fun <T> tryAndLog(title: String, message: String = "", crossinline action: EmptyFunctionResult<T>): T? {
    return tryAndLog(title, message, L::error, action)
}

/**
 *
 */
inline fun <T> tryAndLog(title: String,
                         message: String,
                         logger: loggerFunction,
                         crossinline action: EmptyFunctionResult<T>): T? {
    return try {
        action()
    } catch (exception: Throwable) {
        logger(title, message, exception)
        null
    }
}


suspend inline fun <T> tryAndLogSuspend(title: String,
                                        noinline action: AsyncEmptyFunctionResult<T>): T? {
    return try {
        action()
    } catch (exception: Throwable) {
        L.error(title, "", exception)
        null
    }
}


suspend inline fun <T> tryAndLogSuspend(title: String,
                                        message: String = "",
                                        logger: loggerFunction = L::error,
                                        noinline action: suspend () -> T): T? {
    return try {
        action()
    } catch (exception: Throwable) {
        logger(title, message, exception)
        null
    }
}
