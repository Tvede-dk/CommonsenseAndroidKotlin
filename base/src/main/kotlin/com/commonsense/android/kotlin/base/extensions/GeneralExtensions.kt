@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import android.support.annotation.IntRange
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.base.time.*
import java.lang.ref.*
import java.util.*
import kotlin.system.*


/**
 * Measure time in seconds
 *
 * @param function EmptyFunction the function to measure the time of
 * @return Long the number of seconds it took to execute
 * */
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
 * @receiver T?
 * @param other T? the value to compare to
 * @return Boolean true this is null or is equal to the other object (equals)
 */
inline fun <T> T?.isNullOrEqualTo(other: T?): Boolean = this == null || this == other

/**
 * Creates a weak reference of this object.
 * @receiver T the object to create a weak reference of
 * @return WeakReference<T> a weak reference to the given object
 */
inline fun <T> T.weakReference(): WeakReference<T> = WeakReference(this)

/**
 * Will use the value if the weak reference is not pointing to null
 * This differes from `use` where we cannot use it on an optional value inside of a WeakReference
 */
inline fun <T> WeakReference<T?>.useOpt(crossinline action: T.() -> Unit) {
    get()?.let(action)
}


/**
 * Performs the given action iff this weak reference is not null
 * @receiver WeakReference<T> the weak reference we want to unpack
 * @param action T.() -> Unit the action to run if the reference is not null
 */
inline fun <T> WeakReference<T>.use(crossinline action: T.() -> Unit) {
    get()?.let(action)
}

/**
 * Uses the given weak reaference if available or does the other action
 * @receiver WeakReference<T>
 * @param ifAvailable T.() -> Unit the action to perform iff the weak reference did contain something (not null)
 * @param ifNotAvailable EmptyFunction if the weakreference gave null,this action will be performed
 */
inline fun <T> WeakReference<T>.useRefOr(crossinline ifAvailable: T.() -> Unit,
                                         crossinline ifNotAvailable: EmptyFunction) {
    get().useOr(ifAvailable, ifNotAvailable)
}

/**
 * Uses this value iff not null or another if it is.
 * the first function (argument) will receive the value iff it is not null, the second is witout any parameters
 *
 * @receiver T?
 * @param ifNotNull T.() -> Unit the action to perform iff this is not null
 * @param ifNull EmptyFunction  if the this is null this action will be performed
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
 * @receiver WeakReference<FunctionUnit<T>?> the weak reference
 * @param input T the type of wrapped object
 */
inline fun <T> WeakReference<FunctionUnit<T>?>.use(input: T) {
    get()?.let { it(input) }
}


/**
 * Creates a weak reference, if this is not null. otherwise returns null.
 * @receiver T? the potential not null object
 * @return WeakReference<T>? the weak reference of the object, or null if this parameter was null
 */
inline fun <T> T?.weakReference(): WeakReference<T>? where T : Optional<*> =
        this?.let(::WeakReference)


/**
 * Allows us to call a function iff all is not null (the sender / receiver).
 * @receiver T?
 * @param receiver ((T) -> Unit)?
 * @return T?
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
inline fun <U> Any?.map(ifNotNull: U, ifNull: U): U {
    return this.isNotNull.map(ifNotNull, ifNull)
}

/**
 * Maps an optional value into another value
 * @param ifNotNull the value if 'this' is not null
 * @param ifNull the value if 'this' is null
 * @return the value depending on 'this' value
 */
inline fun <U, T : Any> T?.mapNullLazy(crossinline ifNotNull: Function1<T, U>,
                                       crossinline ifNull: EmptyFunctionResult<U>): U {
    return if (this != null) {
        ifNotNull(this)
    } else {
        ifNull()
    }
}


/**
 * Performs the given action from 0 until this value times.
 * @receiver Int
 * @param action FunctionUnit<Int> The action to invoke each time
 */
inline fun Int.forEach(crossinline action: FunctionUnit<Int>) {
    for (i in 0 until this) {
        action(i)
    }
}


/**
 * Creates a safer cast than regular due to the reified type T.
 * @receiver Any
 * @return T? the casted value iff possible to cast, null otherwise.
 */
inline fun <reified T> Any.cast(): T? = this as? T


/**
 * gets the class type of a given type.
 * this can replace "mytype::class" with a simple "type()", which also makes it possible to
 * refactor and a lot without ever depending on the type.
 * @return Class<T> The requested class type.
 */
inline fun <reified T> type(): Class<T> = T::class.java


/**
 * For each value in [0; value] we create U and put that into a list
 * @receiver @receiver:IntRange(from = 1) Int
 * @param function Function1<Int, U> the mapper function
 * @return List<U> the resulting list
 */
fun <U> @receiver:IntRange(from = 1) Int.mapEach(function: Function1<Int, U>): List<U> {
    return List(this, function)
}