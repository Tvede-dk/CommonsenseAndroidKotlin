package com.commonsense.android.kotlin.example.activity

import com.commonsense.android.kotlin.example.R
import com.commonsense.android.kotlin.system.base.BaseSplashActivity
import com.commonsense.android.kotlin.system.extensions.startActivity

/**
 * Created by Kasper Tvede on 1/29/2018.
 * Purpose:
 *
 */
class SplashExampleActivity : BaseSplashActivity() {
    val showBadSplashUsage = false
    override fun onAppLoaded() {
        if (showBadSplashUsage) {
            setContentView(R.layout.main_activity)
            //above should throw since a real splash screen does not use a layout or anything else; its
            //simply an activity that when loaded dismisses. and uses a special theme.
        }
        startActivity(IntroActivity::class)
    }
}