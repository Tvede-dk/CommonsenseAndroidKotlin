package com.commonsense.android.kotlin.base.algorithms


/**
 * Created by Kasper Tvede
 * The base class for handling calculating the average, with the ability to add more values to it
 * /akk the running average.
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


/**
 *
 */
open class RunningAverageInt : RunningAverageAbstract<Int>() {
    override fun addValues(first: Int, second: Int): Int = first + second
    override val zero: Int
        get() = 0
}

/**
 *
 */
open class RunningAverageDouble : RunningAverageAbstract<Double>() {
    override fun addValues(first: Double, second: Double): Double = first + second
    override val zero: Double
        get() = 0.0
}

/**
 *
 */
open class RunningAverageFloat : RunningAverageAbstract<Float>() {
    override fun addValues(first: Float, second: Float): Float = first + second
    override val zero: Float
        get() = 0f
}


abstract class RunningAverageCappedAbstract<T : Number>(
        private val cappedNumberOfValues: Int) {


    abstract fun clearValues()

    abstract fun setValue(item: T, index: Int)

    abstract fun takeValues(toTakeCount: Int): Iterable<T>


    /**
     * How many values we have set of the available elements in the array
     */
    private var valuesSet = 0
    /**
     * Since the array acts as a ring buffer.
     */
    private var currentIndex = 0


    fun addValue(newValue: T) {
        valuesSet = minOf(valuesSet + 1, cappedNumberOfValues)
        setValue(newValue, currentIndex)
        currentIndex = (currentIndex + 1).rem(cappedNumberOfValues)
    }

    val average: Double
        get() = takeValues(valuesSet).sumByDouble(Number::toDouble) / valuesSet

    fun reset() {
        valuesSet = 0
        currentIndex = 0
        clearValues()
    }

}


open class RunningAverageFloatCapped2(cappedValuesToAverage: Int) : RunningAverageCappedAbstract<Float>(cappedValuesToAverage) {

    private val values = FloatArray(cappedValuesToAverage)

    override fun takeValues(toTakeCount: Int) = values.take(toTakeCount)

    override fun setValue(item: Float, index: Int) {
        values[index] = item
    }

    override fun clearValues() = values.fill(0f)
}

open class RunningAverageIntCapped(cappedValuesToAverage: Int) : RunningAverageCappedAbstract<Int>(cappedValuesToAverage) {

    private val values = IntArray(cappedValuesToAverage)

    override fun clearValues() = values.fill(0)

    override fun setValue(item: Int, index: Int) {
        values[index] = item
    }

    override fun takeValues(toTakeCount: Int): Iterable<Int> = values.take(toTakeCount)
}

open class RunningAverageDoubleCapped(cappedValuesToAverage: Int) : RunningAverageCappedAbstract<Double>(cappedValuesToAverage) {

    private val values = DoubleArray(cappedValuesToAverage)

    override fun clearValues() = values.fill(0.0)


    override fun setValue(item: Double, index: Int) {
        values[index] = item
    }


    override fun takeValues(toTakeCount: Int): Iterable<Double> =
            values.take(toTakeCount)

}


/**
 *
 */
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
