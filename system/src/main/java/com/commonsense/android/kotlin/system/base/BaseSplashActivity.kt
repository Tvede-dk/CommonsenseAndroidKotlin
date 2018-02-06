package com.commonsense.android.kotlin.system.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.system.BadUsageException
import com.commonsense.android.kotlin.system.extensions.safeFinish

/**
 * Created by Kasper Tvede on 1/29/2018.
 * Purpose: handling the splash screen as an activity; it will basically disallow the wrong way to make real splash screens;
 * the intention is to help, educate, and guide, the implementation of a splash screen;
 * calling basically any view related functions will throw a describing exception with the
 *
 */
abstract class BaseSplashActivity : Activity() {

    private val basicDescriptionString = "\n\nAccessing / using the view as/ in a splash screen is wrong\n" +
            "The splash screen should only present the next activity after the app has loaded\n" +
            "this means that you are properly trying to make the splash screen in code;\n" +
            "how to make a proper splash screen in android , see\n" +
            "https://www.youtube.com/watch?v=E5Xu2iNHRkk (App Launch time & Themed launch screens (Android Performance Patterns Season 6 Ep. 4))\n" +
            "or the example splash screen bundled with the library.\n" +
            "https://github.com/Tvede-dk/CommonsenseAndroidKotlin/tree/master/system/src/main/java/com/commonsense/android/kotlin/system/base/BaseSplashActivity.kt\n"

    override fun setContentView(layoutResID: Int) {
        throw BadUsageException(basicDescriptionString)
    }

    override fun setContentView(view: View?) {
        throw BadUsageException(basicDescriptionString)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        throw BadUsageException(basicDescriptionString)
    }

    override fun <T : View> findViewById(id: Int): T {
        throw BadUsageException(basicDescriptionString)
    }


    /**
     * Called when the application is loaded and the splash is ready to be dismissed.
     */
    abstract fun onAppLoaded()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onAppLoaded()
        safeFinish()
    }
}
