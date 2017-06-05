package com.commonsense.android.kotlin.extensions

import kotlin.system.measureNanoTime

/**
 * Created by Kasper Tvede on 05-06-2017.
 */
fun measureSecondTime(function: () -> Unit): Long {
    val time = measureNanoTime(function)
    return time / 10_00_000_00L //nano = 100 millionth of a second
}