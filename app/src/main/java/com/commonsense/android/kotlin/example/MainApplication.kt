package com.commonsense.android.kotlin.example

import com.commonsense.android.kotlin.prebuilt.baseClasses.BaseApplication
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.tools.crash.CrashListener


/**
 * Created by Kasper Tvede on 31-05-2017.
 */

class MainApplication : BaseApplication() {
    override fun isDebugMode(): Boolean = BuildConfig.DEBUG

    override fun afterOnCreate() {
//        if (isDebugMode()) {
            CrashListener.setupListenerGlobally(applicationContext)
//        }
    }

    override fun onApplicationResumed() {
        super.onApplicationResumed()
        safeToast("application resumed")
    }

    override fun onApplicationPaused() {
        super.onApplicationPaused()
        safeToast("application paused.")

    }
}