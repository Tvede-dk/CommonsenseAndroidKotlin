package com.commonsense.android.kotlin.base.algorithms


/**
 * Created by Kasper Tvede on 28-04-2018.
 * Purpose:
 *
 */

abstract class RunningAverageAbstract<T : Number> {

    private var numberCount: Long = 0

    private var aggregatedValue: T

    protected abstract val zero: T

    protected abstract fun addValues(first: T, second: T): T

    init {
        @Suppress("LeakingThis")
        aggregatedValue = zero
    }

    fun addValue(newValue: T) {
        aggregatedValue = addValues(aggregatedValue, newValue)
        numberCount += 1
    }

    val average: Double
        get () = aggregatedValue.toDouble() / numberCount

    fun reset() {
        aggregatedValue = zero
        numberCount = 0
    }
}

open class RunningAverageInt : RunningAverageAbstract<Int>() {
    override fun addValues(first: Int, second: Int): Int = first + second
    override val zero: Int
        get() = 0
}

open class RunningAverageDouble : RunningAverageAbstract<Double>() {
    override fun addValues(first: Double, second: Double): Double = first + second
    override val zero: Double
        get() = 0.0
}

open class RunningAverageFloat : RunningAverageAbstract<Float>() {
    override fun addValues(first: Float, second: Float): Float = first + second
    override val zero: Float
        get() = 0f
}


class RunningAverageFloatCapped(
        private val cappedValuesToAverage: Int) {

    private val values = FloatArray(cappedValuesToAverage)

    private var valueSet = 0
    private var currentIndex = 0

    fun average(): Double {
        return values.take(valueSet).sumByDouble(Float::toDouble) / valueSet
    }

    fun addValue(value: Float) {
        valueSet = minOf(valueSet + 1, cappedValuesToAverage)
        values[currentIndex] = value
        currentIndex = (currentIndex + 1).rem(cappedValuesToAverage)
    }

    fun reset() {
        valueSet = 0
        currentIndex = 0
        values.fill(0f)
    }
}
