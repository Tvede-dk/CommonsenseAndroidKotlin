@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.time

import com.commonsense.android.kotlin.base.time.TimeUnit.*
import kotlinx.coroutines.experimental.*

/**
 * Created by Kasper Tvede on 10-03-2018.
 * Purpose:
 * To provide a way more safe, and simple version of TimeUnit
 *
 * TODO when kotlin is ready use inline classes such that the performance penalty is ~= 0
 *
 *
 *
 * example of time units and the relation:
 * https://en.wikipedia.org/wiki/Unit_of_time#/media/File:Time_units.png
 *
 */

sealed class TimeUnit(val value: Long) {

    private val internalString: String by lazy {
        "$value $prefix"
    }

    abstract val prefix: String

    abstract fun toMilliSeconds(): MilliSeconds

    override fun toString(): String {
        return internalString
    }

    class NanoSeconds(nanoSeconds: Long) : TimeUnit(nanoSeconds) {

        override val prefix: String = "ns"

        override fun toMilliSeconds(): MilliSeconds =
                MilliSeconds(value / milliSecondsToNanoSecondsMultiplier)
    }

    class MilliSeconds(milliseconds: Long) : TimeUnit(milliseconds) {

        override val prefix: String = "ms"

        override fun toMilliSeconds(): MilliSeconds = this
    }

    class Seconds(seconds: Long) : TimeUnit(seconds) {
        override val prefix: String = "s"

        override fun toMilliSeconds(): MilliSeconds =
                MilliSeconds(value * secondsToMillisecondsMultiplier)
    }

    class Minutes(minutes: Long) : TimeUnit(minutes) {

        override val prefix: String = "m"

        override fun toMilliSeconds(): MilliSeconds {
            return MilliSeconds(
                    value *
                            (minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }
    }

    class Hours(hours: Long) : TimeUnit(hours) {

        override val prefix: String = "h"

        override fun toMilliSeconds(): MilliSeconds {
            return MilliSeconds(
                    value *
                            (hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }
    }

    class Days(days: Long) : TimeUnit(days) {

        override val prefix: String = "d"

        override fun toMilliSeconds(): MilliSeconds {
            return MilliSeconds(
                    value *
                            (daysToHoursMultiplier *
                                    hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }
    }

}


//region conversion values
// reason for @Suppress("unused") is that we do not want to clutter up anything,
// so we basically limit theses constants to only be at TimeUnit.
@Suppress("unused")
private val TimeUnit.daysToHoursMultiplier: Long
    get() = 24

@Suppress("unused")
private val TimeUnit.hoursToMinutesMultiplier: Long
    get() = 60

@Suppress("unused")
private val TimeUnit.minutesToSecondsMultiplier: Long
    get() = 60

@Suppress("unused")
private val TimeUnit.secondsToMillisecondsMultiplier: Long
    get() = 1_000

@Suppress("unused")
private val TimeUnit.milliSecondsToNanoSecondsMultiplier: Long
    get() = 1_000_000

//endregion

//region all conversions from nanoseconds
fun NanoSeconds.toSeconds(): Seconds =
        toMilliSeconds().toSeconds()

fun NanoSeconds.toMinutes(): Minutes =
        toSeconds().toMinutes()

fun NanoSeconds.toHours(): Hours =
        toMinutes().toHours()

fun NanoSeconds.toDays(): Days =
        toHours().toDays()

//endregion


//region all conversions from milliseconds

fun MilliSeconds.toNanoSeconds(): NanoSeconds =
        NanoSeconds(value * milliSecondsToNanoSecondsMultiplier)

fun MilliSeconds.toSeconds(): Seconds =
        Seconds(value / secondsToMillisecondsMultiplier)

fun MilliSeconds.toMinutes(): Minutes =
        toSeconds().toMinutes()

fun MilliSeconds.toHours(): Hours =
        toMinutes().toHours()

fun MilliSeconds.toDays(): Days =
        toHours().toDays()


//endregion

//region all conversions from seconds

fun Seconds.toNanoSeconds(): NanoSeconds =
        toMilliSeconds().toNanoSeconds()

fun Seconds.toMinutes(): Minutes =
        Minutes(value / minutesToSecondsMultiplier)

fun Seconds.toHours(): Hours =
        toMinutes().toHours()

fun Seconds.toDays(): Days =
        toHours().toDays()


//endregion

//region all conversions from minutes
fun Minutes.toNanoSeconds(): NanoSeconds =
        toMilliSeconds().toNanoSeconds()

fun Minutes.toSeconds(): Seconds =
        Seconds(value * minutesToSecondsMultiplier)

fun Minutes.toHours(): Hours =
        Hours(value / hoursToMinutesMultiplier)

fun Minutes.toDays(): Days =
        toHours().toDays()


//endregion

//region all conversions from hours

fun Hours.toNanoSeconds(): NanoSeconds =
        toMilliSeconds().toNanoSeconds()

fun Hours.toSeconds(): Seconds =
        toMinutes().toSeconds()

fun Hours.toMinutes(): Minutes =
        Minutes(value * hoursToMinutesMultiplier)

fun Hours.toDays(): Days =
        Days(value / daysToHoursMultiplier)

//endregion

//region all conversions from days

fun Days.toNanoSeconds(): NanoSeconds =
        toMilliSeconds().toNanoSeconds()

fun Days.toSeconds(): Seconds =
        toMinutes().toSeconds()

fun Days.toMinutes(): Minutes =
        toHours().toMinutes()

fun Days.toHours(): Hours =
        Hours(value * daysToHoursMultiplier)


//endregion


/**
 * Sleeps (coroutine) this time unit
 */
suspend fun TimeUnit.delay() {
    delay(this.toMilliSeconds().value)
}
