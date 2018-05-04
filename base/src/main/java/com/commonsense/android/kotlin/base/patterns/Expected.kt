package com.commonsense.android.kotlin.base.patterns

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
interface Expected<out Value> {

    val isError: Boolean

    val value: Value

    val error: Throwable?

    val isValid: Boolean
        get() = !isError

    companion object {
        fun <T> success(value: T): Expected<T> {
            return ExpectedSuccess(value)
        }

        fun <T> failed(exception: Throwable = Exception("Failed with no exception")): Expected<T> {
            return ExpectedFailed(exception)
        }
    }
}

inline fun <T, U> Expected<T>.use(crossinline action: (T) -> U): U? {
    return if (isValid) {
        action(value)
    } else {
        null
    }
}


suspend fun <T, U> Expected<T>.useAsync(action: suspend (T) -> U): U? {
    return if (isValid) {
        action(value)
    } else {
        null
    }
}

class ExpectedFailed<out T>(exception: Throwable) : Expected<T> {

    override val isError: Boolean = true

    override val error: Throwable = exception

    override val value: T
        get() = throw error

}

private class ExpectedSuccess<out T>(successValue: T) : Expected<T> {
    override val value: T = successValue

    override val error: Exception? = null

    override val isError: Boolean = false

}

