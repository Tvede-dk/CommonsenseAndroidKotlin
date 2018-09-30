@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package csense.android.tools

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


