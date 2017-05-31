package com.CommonSenseAndroidKotlin.example

import com.commonsense.android.kotlin.baseClasses.BaseApplication

/**
 * Created by Kasper Tvede on 31-05-2017.
 */

class MainApplication : BaseApplication() {
    override fun isDebugMode(): Boolean = BuildConfig.DEBUG

    override fun afterOnCreate() {

    }

}