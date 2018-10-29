package com.commonsense.android.kotlin.system.base

import android.app.*
import android.os.*
import android.support.annotation.*
import android.view.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.system.*
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*


/**
 * Created by Kasper Tvede on 1/29/2018.
 * Purpose: handling the splash screen as an activity; it will basically disallow the wrong way to make real splash screens;
 * the intention is to help, educate, and guide, the implementation of a splash screen;
 * calling basically any view related functions will throw a describing exception with the
 *
 */
abstract class BaseSplashActivity : Activity() {
    /**
     * The text should say enough.
     * Its the full blown description of what you / the user did wrong.
     */
    private val basicDescriptionString = "\n\nAccessing / using the view as/ in a splash screen is wrong\n" +
            "The splash screen should only present the next activity after the app has loaded\n" +
            "this means that you are properly trying to make the splash screen in code;\n" +
            "how to make a proper splash screen in android , see\n" +
            "https://www.youtube.com/watch?v=E5Xu2iNHRkk (App Launch time & Themed launch screens (Android Performance Patterns Season 6 Ep. 4))\n" +
            "or the example splash screen bundled with the library.\n" +
            "https://github.com/Tvede-dk/CommonsenseAndroidKotlin/tree/master/system/src/main/java/com/commonsense/android/kotlin/system/base/BaseSplashActivity.kt\n"

    //region forbidden function calls for a splash screen (will throw the bad usage exception).
    // The RestrictTo to is to further allow lint to also help "catching" this,
    // although not the correct naming and error message, the error will be highlighted.
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final override fun setContentView(layoutResID: Int): Unit =
            throw BadUsageException(basicDescriptionString)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final override fun setContentView(view: View?): Unit =
            throw BadUsageException(basicDescriptionString)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final override fun setContentView(view: View?, params: ViewGroup.LayoutParams?): Unit =
            throw BadUsageException(basicDescriptionString)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final override fun <T : View> findViewById(id: Int): T =
            throw BadUsageException(basicDescriptionString)
    //endregion


    /**
     * Called when the application is loaded and the splash is ready to be dismissed.
     * You may not call finish or alike, since that is taken care of.
     * all you have to do is start the next activity and or "any other"
     * business logic that needs taken care of before the app is "ready".
     */
    abstract fun onAppLoaded()

    // should give a warning if someone tries to override the onCreate, this is very discouraged.
    // use the onAppLoaded hook.
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        afterOnCreate()
    }

    private fun afterOnCreate() = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
        //start pre loading views. since we are a splash screen, we are "allowed" to take "some"
        //time, thus we can stall the loading (not the ui thread) until we have loaded all the views to preload.
        preloadViews(viewsToPreload)
        //when pre loading is done, then prepare the next screen and start the app.
        onAppLoaded()
        //and close the splash screen
        safeFinish()

    })

    /**
     * Specifies which layouts should be loaded in the background
     * (if that fails, no pre loading will occur for that view).
     */
    abstract val viewsToPreload: LayoutResList

    fun toPrettyString(): String {
        return "Base splash activity - viewsToPreload" +
                viewsToPreload.views.map { "layout id: $it" }.prettyStringContent()
    }

    override fun toString(): String = toPrettyString()
}
