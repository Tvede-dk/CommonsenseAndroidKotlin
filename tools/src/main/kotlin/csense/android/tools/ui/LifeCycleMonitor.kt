package csense.android.tools.ui

import android.app.*
import android.app.Application.*
import android.os.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.logging.*

/**
 *
 */

class LifeCycleMonitor(application: Application) : ActivityLifecycleCallbacks {

    private val activityMap: MutableMap<String, LifeCycleTracking> = mutableMapOf()

    override fun onActivityPaused(activity: Activity) = modifyTracking(activity) {
        state = LifeCycleState.OnPaused
        onPause = currentTime
    }

    override fun onActivityResumed(activity: Activity) = modifyTracking(activity) {
        state = LifeCycleState.OnResumed
        onResume = currentTime
        val onCreateTimeInMs = timeForCreate.toString()
        val fullCreationTime = timeFromStartToDisplay.toString()
        L.error(this::class, "$name: OnCreate time: $onCreateTimeInMs")
        L.error(this::class, "$name: Full creation time: $fullCreationTime")
    }

    override fun onActivityStarted(activity: Activity) = modifyTracking(activity) {
        state = LifeCycleState.OnStarted
        onStarted = currentTime
    }

    override fun onActivityDestroyed(activity: Activity) {
        // summarize tracking ? hmm
        activityMap.remove(activity.name)

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = modifyTracking(activity) {
        state = LifeCycleState.OnSaving
        onSavedInstanceState = currentTime
    }

    override fun onActivityStopped(activity: Activity) = modifyTracking(activity) {
        state = LifeCycleState.OnStopped
        onStopped = currentTime
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) = modifyTracking(activity) {
        state = LifeCycleState.OnCreating
        onCreate = currentTime
    }


    init {
        application.registerActivityLifecycleCallbacks(this)
    }


    /**
     * default action on each invocation.
     * creates a tracking if non is there, and parses that into the callback as the receiver)
     * stops execution if the activity is null.
     *
     */
    private inline fun modifyTracking(activity: Activity?, callback: EmptyReceiver<LifeCycleTracking>) {
        if (activity == null) {
            return
        }
        activityMap.createOrUse(
                activity.name,
                { LifeCycleTracking(activity.name) },
                {
                    callback(it)
                    L.debug(this::class,
                            "current tracking is: $it"
                    )
                })
    }

    private inline val Activity.name
        get() = javaClass.simpleName


    private inline val currentTime: Long
        get() = System.nanoTime()

}




