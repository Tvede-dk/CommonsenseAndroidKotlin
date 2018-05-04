package com.commonsense.android.kotlin.base.extensions

import android.text.Editable
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.base.time.delay
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
fun measureSecondTime(function: EmptyFunction): Long {
    val time = measureNanoTime(function)
    return time / 10_00_000_00L //nano = 100 millionth of a second
}


/**
 * Measure the time of some occurence with a timeout. the function waits (coroutine delay) until the given timeout.
 *
 * @param waitingTimeUnit the time to wait for the response to come.
 * @param signalAsync the schedule to measure the time, supplies the time back it was called.
 * @return the delta time for the start of the operation to the "optional" ending,
 * if the ending have not occurred then it returns null
 */
suspend inline fun measureOptAsyncCallback(waitingTimeUnit: TimeUnit,
                                           crossinline signalAsync: FunctionUnit<FunctionUnit<TimeUnit>>)
        : TimeUnit.Milliseconds? {
    var optionalEnd: Long = 0
    var didSet = false
    val start = System.currentTimeMillis()
    signalAsync({
        optionalEnd = it.toMilliseconds().milliSeconds
        didSet = true
    })
    waitingTimeUnit.delay()
    return didSet.map(
            ifTrue = TimeUnit.Milliseconds(optionalEnd - start),
            ifFalse = null)
}

/**
 * Measure the time of some occurence with a timeout. the function waits (coroutine delay) until the given timeout.
 *
 * @param waitingTimeUnit the time to wait for the response to come.
 * @param signalAsync signals that we are done /callback. time is taken when the callback is called.
 * @return the delta time for the start of the operation to the "optional" ending,
 * if the ending have not occurred then it returns null
 */
suspend inline fun measureOptAsync(waitingTimeUnit: TimeUnit,
                                   crossinline signalAsync: FunctionUnit<EmptyFunction>)
        : TimeUnit.Milliseconds? =
        measureOptAsyncCallback(waitingTimeUnit, { function: (TimeUnit) -> Unit ->
            function(TimeUnit.Milliseconds(System.currentTimeMillis()))
        })


/**
 * converts an immutable string to an editable edition :)
 */
fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

/**
 * returns true if this is null
 */
val Any?.isNull
    get() = this == null

/**
 * returns true if this is not null.
 */
val Any?.isNotNull
    get() = this != null


/**
 * returns true if this is null or equal to the given argument.
 * does not return true if we are not null but the argument is null.
 */
fun <T> T?.isNullOrEqualTo(other: T?): Boolean = this == null || this == other

/**
 * Creates a weak reference to this object.
 */
fun <T> T.weakReference(): WeakReference<T> = WeakReference(this)

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
 * invokes the function ( wrapped in a weakReference) with the input, if the weakreference is not pointing to null
 */
fun <T> WeakReference<FunctionUnit<T>?>.use(input: T) {
    get()?.let { it(input) }
}


/**
 * Creates a weak reference, if this is not null. otherwise returns null.
 */
fun <T> T?.weakReference(): WeakReference<T>? where T : Optional<*> = this?.let(::WeakReference)

/**
 * Allows us to call a function iff all is not null (the sender / receiver).
 */
fun <T> T?.parseTo(receiver: ((T) -> Unit)?): T? {
    if (this != null && receiver != null) {
        receiver(this)
    }
    return this
}

inline fun Int.forEach(crossinline action: FunctionUnit<Int>) {
    for (i in 0 until this) {
        action(i)
    }
}

/**
 * Maps an optional value into another value
 * @param ifNotNull the value if 'this' is not null
 * @param ifNull the value if 'this' is null
 * @return the value depending on 'this' value
 */
fun <T, U> T?.map(ifNotNull: U, ifNull: U): U {
    return this.isNotNull.map(ifNotNull, ifNull)
}

/**
 * Creates a safer cast than regular due to the reified type T.
 */
inline fun <reified T> Any.cast(): T? = this as? T
