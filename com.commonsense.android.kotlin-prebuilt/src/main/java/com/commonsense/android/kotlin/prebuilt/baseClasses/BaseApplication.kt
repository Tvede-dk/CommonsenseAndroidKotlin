package com.commonsense.android.kotlin.prebuilt.baseClasses

import android.app.Application
import android.os.StrictMode
import android.support.v7.app.AppCompatDelegate
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.extensions.isApiLowerThan
import com.commonsense.android.kotlin.system.logging.logDebug
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Kasper Tvede on 28-10-2016.
 */

abstract class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            logDebug("Spawning analyzer procees. skipping setup")
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        isDebugMode().ifTrue { setupDebugTools() }
        setupVectorDrawableOldAndroid()
        afterOnCreate()
    }

    /**
     * fixes vector drawables on older andorid's.
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
    private fun setupDebugTools() {
        logDebug("Setting up debugging tools")
        enableLeakCanary()
        enableStrictMode()
    }

    private fun enableLeakCanary() {
        logDebug("Setting up leak canary")
        LeakCanary.install(this)
    }

    private fun enableStrictMode() {
        logDebug("Setting up strictMode")
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().
                        detectAll().
                        penaltyLog().
                        penaltyFlashScreen().
                        build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog().penaltyDeath().build())
    }
    //</editor-fold>


}