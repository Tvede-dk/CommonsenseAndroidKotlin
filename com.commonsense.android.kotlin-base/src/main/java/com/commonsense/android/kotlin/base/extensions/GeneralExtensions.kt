package com.commonsense.android.kotlin.base.extensions

import android.text.Editable
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.FunctionUnit
import java.lang.ref.WeakReference
import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 05-06-2017.
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
fun <T> WeakReference<T?>.use(action: T.() -> Unit) {
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
fun <T> T.optWeakReference(): WeakReference<T>? = this?.let(::WeakReference)