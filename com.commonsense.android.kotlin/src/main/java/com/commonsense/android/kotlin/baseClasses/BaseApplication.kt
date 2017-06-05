package com.commonsense.android.kotlin.baseClasses

import android.app.Application
import android.os.StrictMode
import android.support.v7.app.AppCompatDelegate
import com.commonsense.android.kotlin.android.extensions.isApiLowerThan
import com.commonsense.android.kotlin.android.logging.logDebug
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Kasper Tvede on 28-10-2016.
 */

abstract class BaseApplication : Application() {


    fun setupDebugTools() {
        logDebug("Setting up debugging tools")
        enableLeakCanary()
        enableStrictMode()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            logDebug("Spawning analyzer procees. skipping setup")
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        isDebugMode().let { setupDebugTools() }
        setupVectorDrawableOldAndroid()
        afterOnCreate()
    }

    private fun setupVectorDrawableOldAndroid() {
        if (isApiLowerThan(21)) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    abstract fun isDebugMode(): Boolean

    /**
     * this should be overridden as the new "on create", as it allows this class to avoid calling unnecessary (eg if the process is not the main application).
     */
    abstract fun afterOnCreate()

    fun enableLeakCanary() {
        logDebug("Setting up leak canary")
        LeakCanary.install(this)
    }

    fun enableStrictMode() {
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


}