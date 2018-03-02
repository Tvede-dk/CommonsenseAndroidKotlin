package com.commonsense.android.kotlin.tools

import kotlinx.coroutines.experimental.delay

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
enum class NotificationType {
    /**
     * Provokes a crash
     */
    Crash,
    /**
     * only logs the notification
     */
    Logging,
    /**
     * Logs the notification then crashes the application
     */
    CrashAndLogging,
    /**
     * Shows an inline overlay with the notification
     */
    Overlay,
    /**
     *  Shows an inline overlay with the notification and logs it.
     */
    OverlayLogging
}

//TODO move to a suitable location.
sealed class TimeUnit(protected val value: Long, val javaTimeUnit: java.util.concurrent.TimeUnit) {
    abstract fun toMilliseconds(): Milliseconds

    class NanoSeconds(nanoSeconds: Long)
        : TimeUnit(nanoSeconds, java.util.concurrent.TimeUnit.NANOSECONDS) {
        override fun toMilliseconds(): Milliseconds = Milliseconds(value / 1000000)

    }

    class Milliseconds(milliseconds: Long) : TimeUnit(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS) {
        override fun toMilliseconds(): Milliseconds = this
        fun getMilliseconds(): Long {
            return value
        }
    }

    class Seconds(seconds: Long) : TimeUnit(seconds, java.util.concurrent.TimeUnit.SECONDS) {
        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(value * secondsToMillisecondsMultiplier)
        }
    }

    class Minutes(minutes: Long) : TimeUnit(minutes, java.util.concurrent.TimeUnit.MINUTES) {
        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }

    }

    class Hours(hours: Long) : TimeUnit(hours, java.util.concurrent.TimeUnit.HOURS) {
        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }
    }

    class Days(days: Long) : TimeUnit(days, java.util.concurrent.TimeUnit.DAYS) {
        override fun toMilliseconds(): Milliseconds {
            return Milliseconds(
                    value *
                            (daysToHoursMultiplier *
                                    hoursToMinutesMultiplier *
                                    minutesToSecondsMultiplier *
                                    secondsToMillisecondsMultiplier))
        }
    }

    internal val daysToHoursMultiplier: Long = 24
    internal val hoursToMinutesMultiplier: Long = 60
    internal val minutesToSecondsMultiplier: Long = 60
    internal val secondsToMillisecondsMultiplier: Long = 1000

}


/**
 * Sleeps (coroutine) this time unit
 */
suspend fun TimeUnit.delay() {
    delay(5000)
}


