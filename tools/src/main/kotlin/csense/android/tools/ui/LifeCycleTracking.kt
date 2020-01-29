package csense.android.tools.ui

import com.commonsense.android.kotlin.base.time.*
import com.commonsense.android.kotlin.base.time.TimeUnit.*
import csense.android.tools.ui.LifeCycleState.*

data class LifeCycleTracking(val name: String,
                             var state: LifeCycleState = OnCreating,
                             var onCreate: Long = 0,
                             var onPause: Long = 0,
                             var onResume: Long = 0,
                             var onStarted: Long = 0,
                             var onDestroy: Long = 0,
                             var onSavedInstanceState: Long = 0,
                             var onStopped: Long = 0)


val LifeCycleTracking.timeFromStartToDisplay: MilliSeconds
//the time between onCreate and onStarted?
    get() = NanoSeconds(onResume - onCreate).toMilliSeconds()

val LifeCycleTracking.timeForCreate: MilliSeconds
    get () = NanoSeconds(onStarted - onCreate).toMilliSeconds()

enum class LifeCycleState {
    OnCreating, OnStarted, OnResumed, OnPaused, OnStopped, OnSaving
}