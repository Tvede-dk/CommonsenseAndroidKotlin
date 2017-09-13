package com.CommonSenseAndroidKotlin.example

import com.commonsense.android.kotlin.prebuilt.baseClasses.BaseApplication
import com.commonsense.android.kotlin.system.extensions.safeToast


/**
 * Created by Kasper Tvede on 31-05-2017.
 */

class MainApplication : BaseApplication() {
    override fun isDebugMode(): Boolean = BuildConfig.DEBUG

    override fun afterOnCreate() {

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