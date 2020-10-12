@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package csense.android.tools.tracker

import android.app.*
import android.os.*
import com.commonsense.android.kotlin.system.base.interfaceBases.*
import csense.android.tools.tracker.ActivityTrackerEvents.*

/**
 * A tracker for tracking activity events, and recording those.
 *
 */
class ActivityTracker(val application: Application) : BaseActivityLifecycleCallbacks {

    private val eventList: MutableList<ActivityTrackerEvents> = mutableListOf()

    override fun onActivityResumed(activity: Activity) {
        val lastEvent = eventList.lastOrNull() ?: return
        if (lastEvent.data.activityName == activity::class.java.simpleName
                && lastEvent is Stops) {
            eventList.add(ReturnsTo(activity.getTrackingData()))
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        eventList.add(Closes(activity.getTrackingData()))
    }

    override fun onActivityStopped(activity: Activity) {
        eventList.add(Stops(activity.getTrackingData()))

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        eventList.add(GoesTo(activity.getTrackingData()))
    }

    fun register() = application.registerActivityLifecycleCallbacks(this)

    fun deregister() = application.unregisterActivityLifecycleCallbacks(this)
}

sealed class ActivityTrackerEvents(val data: ActivityTrackingData) {
    class GoesTo(data: ActivityTrackingData) : ActivityTrackerEvents(data)
    class ReturnsTo(data: ActivityTrackingData) : ActivityTrackerEvents(data)
    class Closes(data: ActivityTrackingData) : ActivityTrackerEvents(data)
    class Stops(data: ActivityTrackingData) : ActivityTrackerEvents(data)

}

data class ActivityTrackingData(
        val activityName: String,
        val eventAtNanoTime: Long)


fun Activity.getTrackingData(): ActivityTrackingData {
    return ActivityTrackingData(this::class.java.simpleName, System.nanoTime())
}