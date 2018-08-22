package com.commonsense.android.kotlin.base.extensions

import android.support.annotation.IntRange
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.base.time.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede
 *
 */

/**
 * Measure time in seconds
 * returns the time in seconds.
 */
inline fun measureSecondTime(crossinline function: EmptyFunction): Long {
    return TimeUnit.NanoSeconds(measureNanoTime(function)).toSeconds().value
}


/**
 * Measure the time of some occurrence with a timeout.
 * the function waits (coroutine delay) until the given timeout.
 *
 * @param waitingTimeUnit the time to wait for the response to come.
 * @param signalAsync the schedule to measure the time, supplies the time back it was called.
 * @return the delta time for the start of the operation to the "optional" ending,
 * if the ending have not occurred then it returns null
 */
suspend inline fun measureOptAsyncCallback(waitingTimeUnit: TimeUnit,
                                           crossinline signalAsync: FunctionUnit<FunctionUnit<TimeUnit>>)
        : TimeUnit.MilliSeconds? {
    var optionalEnd: Long = 0
    var didSet = false
    val start = System.currentTimeMillis()
    signalAsync {
        optionalEnd = it.toMilliSeconds().value
        didSet = true
    }
    waitingTimeUnit.delay()
    return didSet.map(
            ifTrue = TimeUnit.MilliSeconds(optionalEnd - start),
            ifFalse = null)
}


/**
 * returns true if this is null
 */
inline val Any?.isNull
    get() = this == null

/**
 * returns true if this is not null.
 */
inline val Any?.isNotNull
    get() = this != null


/**
 * returns true if this is null or equal to the given argument.
 * does not return true if we are not null but the argument is null.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.isNullOrEqualTo(other: T?): Boolean = this == null || this == other

/**
 * Creates a weak reference to this object.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T.weakReference(): WeakReference<T> = WeakReference(this)

/**
 * Will use the value if the weak reference is not pointing to null
 */
inline fun <T> WeakReference<T?>.useOpt(crossinline action: T.() -> Unit) {
    get()?.let(action)
}


inline fun <T> WeakReference<T>.use(crossinline action: T.() -> Unit) {
    get()?.let(action)
}

/**
 * Uses the given weak reaference if available or does the other action
 */
inline fun <T> WeakReference<T>.useRefOr(crossinline ifAvailable: T.() -> Unit,
                                         crossinline ifNotAvailable: EmptyFunction) {
    get().useOr(ifAvailable, ifNotAvailable)
}

/**
 * uses this value iff not null or another if it is.
 * the first function (argument) will receive the value iff it is not null, the second is witout any parameters
 *
 */
inline fun <T> T?.useOr(crossinline ifNotNull: T.() -> Unit,
                        crossinline ifNull: EmptyFunction) {
    if (this != null) {
        ifNotNull(this)
    } else {
        ifNull()
    }
}

/**
 * invokes the function ( wrapped in a weakReference) with the input, if the weakreference is not pointing to null
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> WeakReference<FunctionUnit<T>?>.use(input: T) {
    get()?.let { it(input) }
}


/**
 * Creates a weak reference, if this is not null. otherwise returns null.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.weakReference(): WeakReference<T>? where T : Optional<*> =
        this?.let(::WeakReference)

/**
 * Allows us to call a function iff all is not null (the sender / receiver).
 */
fun <T> T?.parseTo(receiver: ((T) -> Unit)?): T? {
    if (this != null && receiver != null) {
        receiver(this)
    }
    return this
}

/**
 * Maps an optional value into another value
 * @param ifNotNull the value if 'this' is not null
 * @param ifNull the value if 'this' is null
 * @return the value depending on 'this' value
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <U> Any?.map(ifNotNull: U, ifNull: U): U {
    return this.isNotNull.map(ifNotNull, ifNull)
}

/**
 * Maps an optional value into another value
 * @param ifNotNull the value if 'this' is not null
 * @param ifNull the value if 'this' is null
 * @return the value depending on 'this' value
 */
inline fun <U, T : Any> T?.mapLazy(crossinline ifNotNull: Function1<T, U>,
                                   crossinline ifNull: EmptyFunctionResult<U>): U {
    return if (this != null) {
        ifNotNull(this)
    } else {
        ifNull()
    }
}

/**
 * Performs the given action from 0 until this value times.
 */
inline fun Int.forEach(crossinline action: FunctionUnit<Int>) {
    for (i in 0 until this) {
        action(i)
    }
}

/**
 * Creates a safer cast than regular due to the reified type T.
 */
inline fun <reified T> Any.cast(): T? = this as? T


/**
 * gets the class type of a given type.
 * this can replace "mytype::class" with a simple "type()", which also makes it possible to
 * refactor and a lot without ever depending on the type.
 */
inline fun <reified T> type(): Class<T> = T::class.java

/**
 *
 * For each value in [0; value] we create U and put that into a list
 */
fun <U> @receiver:IntRange(from = 1) Int.mapEach(function: Function1<Int, U>): List<U> {
    return List(this, function)
}