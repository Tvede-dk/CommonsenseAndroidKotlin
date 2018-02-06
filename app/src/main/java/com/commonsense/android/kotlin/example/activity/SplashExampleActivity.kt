package com.commonsense.android.kotlin.example.activity

import com.commonsense.android.kotlin.example.R
import com.commonsense.android.kotlin.system.base.BaseSplashActivity

/**
 * Created by Kasper Tvede on 1/29/2018.
 * Purpose:
 *
 */
class SplashExampleActivity : BaseSplashActivity() {
    val showBadSplashUsage = true
    override fun onAppLoaded() {

        if (showBadSplashUsage) {
            setContentView(R.layout.activity_main)
            //above should throw since a real splash screen does not use a layout or anything else; its
            //simply an activity that when loaded dismisses. and uses a special theme.
        }

        if (isLoggedIn()) {
            showLoggedInActivity()
        } else {
            showLoginActivity()
        }
    }

    fun isLoggedIn(): Boolean {
        return false
    }

    fun showLoggedInActivity() {

    }

    fun showLoginActivity() {

    }
}