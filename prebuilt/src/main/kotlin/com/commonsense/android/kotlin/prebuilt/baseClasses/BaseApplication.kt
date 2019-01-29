@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.prebuilt.baseClasses

import android.app.*
import android.os.*
import android.os.strictmode.*
import androidx.annotation.*
import androidx.appcompat.app.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import com.squareup.leakcanary.*
import java.util.concurrent.atomic.*

/**
 * Created by Kasper Tvede on 28-10-2016.
 */

abstract class BaseApplication : Application() {

    private val activityCounter by lazy {
        ActivityCounter().apply {
            onZeroCallback = this@BaseApplication::onApplicationPaused
            onNonZeroCallback = this@BaseApplication::onApplicationResumed
        }
    }

    /**
     * when the "application" is visible / resumed, which means at least one activity is visible
     */
    open fun onApplicationResumed() {

    }

    /**
     * when the "application" is invisible / paused, which means at no activity is visible at this time / point.
     * It could be a "re-config". / orientation
     */
    open fun onApplicationPaused() {

    }


    override fun onCreate() {
        super.onCreate()
        if (shouldBailOnCreate() != false) {
            return
        }
        registerActivityLifecycleCallbacks(activityCounter)
        isDebugMode().ifTrue { setupDebugTools() }
        setupVectorDrawableOldAndroid()
        afterOnCreate()
    }

    /**
     * Function handling the checking if we are a special process (required for eg leak canary).
     */
    fun shouldBailOnCreate(): Boolean? = tryAndLog(BaseApplication::class) {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            logDebug("Spawning analyzer procees. skipping setup")
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return@tryAndLog true
        }
        return@tryAndLog false
    }

    /**
     * fixes vector drawables on older androids.
     */
    private fun setupVectorDrawableOldAndroid() {
        if (isApiLowerThan(21)) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    /**
     * if returns true, then we are in debug mode / enables all debugging setup.
     */
    abstract fun isDebugMode(): Boolean

    /**
     * this should be overridden as the new "on create", as it allows this class to avoid calling unnecessary (eg if the process is not the main application).
     */
    abstract fun afterOnCreate()

    //<editor-fold desc="Debug tools">
    private fun setupDebugTools() = tryAndLog(BaseApplication::class) {
        logDebug("Setting up debugging tools")
        enableLeakCanary()
        enableStrictMode()
    }

    private fun enableLeakCanary() = tryAndLog(BaseApplication::class) {

        logDebug("Setting up leak canary")
        LeakCanary.install(this)
    }

    private fun enableStrictMode() = tryAndLog(BaseApplication::class) {
        logDebug("Setting up strictMode")
        val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder().apply {
            configureStrictModeThreadPolicy(this)
            shouldDieOnStrictModeViolation().ifTrue { penaltyDeath() }
            ifApiIsEqualOrGreater(28) {
                penaltyListener(mainExecutor, StrictMode.OnThreadViolationListener { onStrictModeViolationOptional(it) })
            }

        }
        StrictMode.setThreadPolicy(threadPolicyBuilder.build())

        val vmPolicyBuilder = StrictMode.VmPolicy.Builder().apply {
            configureStrictModeVMPolicy(this)
            shouldDieOnStrictModeViolation().ifTrue { penaltyDeath() }
            ifApiIsEqualOrGreater(28) {
                penaltyListener(mainExecutor, StrictMode.OnVmViolationListener { onStrictModeViolationOptional(it) })
            }
        }
        StrictMode.setVmPolicy(vmPolicyBuilder.build())
    }

    open fun configureStrictModeThreadPolicy(builder: StrictMode.ThreadPolicy.Builder) {
        builder.apply {
            detectAll()
            penaltyLog()
            penaltyFlashScreen()
        }
    }


    open fun configureStrictModeVMPolicy(builder: StrictMode.VmPolicy.Builder) {
        builder.apply {
            detectAll()
            penaltyLog()
        }
    }

    /**
     * Will be called for each strict mode violation
     * on api >= 28
     */
    @RequiresApi(28)
    open fun onStrictModeViolation(violation: Violation) {
    }

    @RequiresApi(28)
    private fun onStrictModeViolationOptional(violation: Violation?): Unit = violation?.let {
        onStrictModeViolation(it)
    } ?: Unit


    /**
     * if returns true, then a violation will cause "deatch"
     * default if false,which only logs then.
     */
    open fun shouldDieOnStrictModeViolation(): Boolean {
        return false
    }
    //</editor-fold>
}


/**
 *
 */
class ActivityCounter : Application.ActivityLifecycleCallbacks {

    /**
     * when the running activities reach 0, this gets called
     */
    var onZeroCallback: EmptyFunction? = null
    /**
     * when the running activities reach more than 0, this gets called
     */
    var onNonZeroCallback: EmptyFunction? = null

    private val runningActivites = AtomicInteger(0)

    override fun onActivityPaused(p0: Activity?) {
    }

    override fun onActivityResumed(p0: Activity?) {

    }

    override fun onActivityDestroyed(p0: Activity?) {
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    /**
     * Count "just started" activities.
     */
    override fun onActivityStarted(p0: Activity?) {
        (runningActivites.incrementAndGet() == 1).ifTrue { onNonZeroCallback?.invoke() }
    }

    /**
     * Count "not dead" but close to activities
     */
    override fun onActivityStopped(p0: Activity?) {
        runningActivites.decrementAndGet().isZero.ifTrue { onZeroCallback?.invoke() }

    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }

}
