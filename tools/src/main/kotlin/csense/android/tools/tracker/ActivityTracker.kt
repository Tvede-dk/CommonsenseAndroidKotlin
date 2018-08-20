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

    override fun onActivityResumed(activity: Activity?) {
        val lastEvent = eventList.lastOrNull()
        if (activity == null || lastEvent == null) {
            return
        }
        if (lastEvent.data.activityName == activity::class.java.simpleName
                && lastEvent is Stops) {
            eventList.add(ReturnsTo(activity.getTrackingData()))
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        val act = activity ?: return
        eventList.add(Closes(act.getTrackingData()))
    }

    override fun onActivityStopped(activity: Activity?) {
        val act = activity ?: return
        eventList.add(Stops(act.getTrackingData()))

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        val act = activity ?: return
        eventList.add(GoesTo(act.getTrackingData()))
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