package com.commonsense.android.kotlin.base.time

/**
 * Created by Kasper Tvede on 10-03-2018.
 * Purpose:
 * To provide a way more safe, and simple version of TimeUnit
 *
 * TODO when kotlin is ready use inline classes such that the performance penalty is ~= 0
 *
 */

sealed class TimeUnit(protected val value: Long) {
    abstract fun toMilliseconds(): Milliseconds


    abstract override fun toString(): String

    class NanoSeconds(nanoSeconds: Long)
        : TimeUnit(nanoSeconds) {
        override fun toString(): String {
            return "$value ns"
        }

        override fun toMilliseconds(): Milliseconds =
                Milliseconds(value / milliSecondsToNanoSecondsMultiplier)

        val nanoSeconds
            get () = value

    }

    class Milliseconds(milliseconds: Long) : TimeUnit(milliseconds) {
        override fun toString(): String {
            return "$value ms"
        }

        override fun toMilliseconds(): Milliseconds = this

        val milliSeconds
            get() = value

        fun toNanoSeconds(): NanoSeconds =
                NanoSeconds(value * milliSecondsToNanoSecondsMultiplier)

        fun toSeconds(): Seconds =
                Seconds(value / secondsToMillisecondsMultiplier)
    }

    class Seconds(seconds: Long) : TimeUnit(seconds) {
        override fun toString(): String {
            return "$value s"
        }

        override fun toMilliseconds(): Milliseconds =
                Milliseconds(value * secondsToMillisecondsMultiplier)

        fun toMinutes(): Minutes = Minutes(value / minutesToSecondsMultiplier)
    }

    class Minutes(minutes: Long) : TimeUnit(minutes) {

        override fun toString(): String {
            return "$value m"
        }

        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }

        fun toSeconds(): Seconds {
            return Seconds(value * minutesToSecondsMultiplier)
        }

        fun toHours(): Hours {
            return Hours(value / hoursToMinutesMultiplier)
        }

    }

    class Hours(hours: Long) : TimeUnit(hours) {

        override fun toString(): String {
            return "$value h"
        }

        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }

        fun toMinutes(): Minutes {
            return Minutes(value * hoursToMinutesMultiplier)
        }

        fun toDays(): Days {
            return Days(value / daysToHoursMultiplier)
        }
    }

    class Days(days: Long) : TimeUnit(days) {
        override fun toString(): String {
            return "$value d"
        }

        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (daysToHoursMultiplier *
                                    hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }

        fun toHours(): Hours {
            return Hours(value * daysToHoursMultiplier)
        }
    }
}

private val TimeUnit.daysToHoursMultiplier: Long
    get() = 24

private val TimeUnit.hoursToMinutesMultiplier: Long
    get() = 60

private val TimeUnit.minutesToSecondsMultiplier: Long
    get() = 60

private val TimeUnit.secondsToMillisecondsMultiplier: Long
    get() = 1_000

private val TimeUnit.milliSecondsToNanoSecondsMultiplier: Long
    get() = 1_000_000


/**
 * Sleeps (coroutine) this time unit
 */
suspend fun TimeUnit.delay() {
    kotlinx.coroutines.experimental.delay(this.toMilliseconds().milliSeconds)
}
