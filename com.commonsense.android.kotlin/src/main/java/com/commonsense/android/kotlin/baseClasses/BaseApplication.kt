package com.commonsense.android.kotlin.baseClasses

import android.app.Application
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Kasper Tvede on 28-10-2016.
 */

abstract class BaseApplication : Application() {


    fun setupDebugTools() {
        enableLeakCanary()
        enableStricMode()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        isDebugMode().let { setupDebugTools() }
        afterOnCreate()
    }

    abstract fun isDebugMode(): Boolean;

    /**
     * this should be overriden as the new "on create", as it allows this class to avoid calling unnessary (eg if the process is not the main application).
     */
    abstract fun afterOnCreate()

    fun enableLeakCanary() {
        LeakCanary.install(this)
    }

    fun enableStricMode() {
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().
                        detectAll().
                        penaltyLog().
                        penaltyFlashScreen().
                        build())

        StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().build())
    }


}