package com.commonsense.android.kotlin.prebuilt.baseClasses

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatDelegate
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.base.extensions.isZero
import com.commonsense.android.kotlin.system.extensions.ifApiIsEqualOrGreater
import com.commonsense.android.kotlin.system.extensions.ifApiLowerThan
import com.commonsense.android.kotlin.system.logging.logDebug
import com.commonsense.android.kotlin.system.logging.tryAndLog
import com.squareup.leakcanary.LeakCanary
import java.util.concurrent.atomic.AtomicInteger

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


    open fun onApplicationResumed() {

    }

    open fun onApplicationPaused() {

    }

    override fun onCreate() {
        super.onCreate()
        // install multidexing if needed.
        ifApiLowerThan(21) {
            MultiDex.install(this)
        }

        //make sure that we are to bail iff required.
        if (shouldBailOnCreate() != false) {
            return
        }
        // register the lifecycle listener
        registerActivityLifecycleCallbacks(activityCounter)
        // setup debug tools if debug mode.
        isDebugMode().ifTrue { setupDebugTools() }
        // patch older androids vector drawable.
        setupVectorDrawableOldAndroid()
        afterOnCreate()
    }

    /**
     * Function handling the checking if we are a special process (required for eg leak canary).
     */
    private fun shouldBailOnCreate() = tryAndLog(BaseApplication::class) {
        val isAnalyzer = LeakCanary.isInAnalyzerProcess(this)
        return@tryAndLog isAnalyzer.ifTrue {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            logDebug("Spawning analyzer process. skipping setup")
        }
    }

    /**
     * fixes vector drawables on older androids.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun setupVectorDrawableOldAndroid() = ifApiLowerThan(21) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
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

    @Suppress("NOTHING_TO_INLINE")
    private inline fun enableLeakCanary() {
        logDebug("Setting up leak canary")
        LeakCanary.install(this)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun enableStrictMode() {
        logDebug("Setting up strictMode")
        StrictMode.setThreadPolicy(createStrictModeThreadPolicy())
        StrictMode.setVmPolicy(createStrictModeVmPolicy())

    }


    /**
     * Hook point for overriding the strict mode threading policy
     */
    @Suppress("NOTHING_TO_INLINE")
    protected inline fun createStrictModeThreadPolicy(): StrictMode.ThreadPolicy {
        return StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
                .build()
    }

    /**
     * hook point for overriding the strict mode vm policy
     */
    @Suppress("NOTHING_TO_INLINE")
    protected inline fun createStrictModeVmPolicy(): StrictMode.VmPolicy {
        return StrictMode.VmPolicy.Builder()
                .detectAll().apply {
                    ifApiIsEqualOrGreater(23) {
                        detectCleartextNetwork()
                    }
                }
                .penaltyLog().penaltyDeath().build()
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

    private val runningActivities = AtomicInteger(0)

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
        (runningActivities.incrementAndGet() == 1).ifTrue { onNonZeroCallback?.invoke() }
    }

    /**
     * Count "not dead" but close to activities
     */
    override fun onActivityStopped(p0: Activity?) {
        runningActivities.decrementAndGet().isZero.ifTrue { onZeroCallback?.invoke() }

    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }

}
