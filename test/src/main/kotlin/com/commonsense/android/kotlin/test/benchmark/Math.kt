package com.commonsense.android.kotlin.test.benchmark

/**
 * Created by Kasper Tvede on 14-04-2018.
 * Purpose:
 *
 */
fun Iterable<Number>.calculateSD(): Double {
    return calculateSDFrom(count(), this::elementAt)
}

fun <T : Number> Array<T>.calculateSD(): Double {
    return calculateSDFrom(count(), this::get)
}

fun DoubleArray.calculateSD(): Double {
    return calculateSDFrom(size, this::get)
}

fun FloatArray.calculateSD(): Double {
    return calculateSDFrom(size, this::get)
}

private inline fun <T> Int.forEach(crossinline action: (Int) -> T) {
    for (i in 0 until this) {
        action(i)
    }
}

private inline fun Int.sumBy(crossinline retriever: (Int) -> Number): Double {
    var sum = 0.0
    forEach {
        sum += retriever(it).toDouble()
    }
    return sum
}

private inline fun calculateSDFrom(count: Int,
                                   crossinline retriever: (index: Int) -> Number): Double {
    val sum = count.sumBy(retriever)
    val mean = sum / count

    val sd = count.sumBy {
        val element = retriever(it)
        Math.pow(element.toDouble() - mean, 2.0)
    }
    val divider = count - 1
    return Math.sqrt(sd / divider)
}
