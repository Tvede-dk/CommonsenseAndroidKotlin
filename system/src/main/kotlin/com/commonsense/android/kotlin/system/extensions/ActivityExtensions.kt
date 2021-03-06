@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.app.*
import android.content.*
import android.support.annotation.*
import android.support.v4.app.*
import android.support.v4.app.Fragment
import android.support.v4.widget.*
import android.support.v7.app.*
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import kotlin.reflect.*


/**
 *
 * @receiver AppCompatActivity
 * @param drawer DrawerLayout
 * @param toolbar Toolbar
 * @param openTitle Int
 * @param closeTitle Int
 * @return ActionBarDrawerToggle
 */
@UiThread
fun AppCompatActivity.setupToolbarAppDrawer(drawer: DrawerLayout,
                                            toolbar: Toolbar,
                                            @StringRes openTitle: Int,
                                            @StringRes closeTitle: Int): ActionBarDrawerToggle {
    setSupportActionBar(toolbar)
    val toggle = ActionBarDrawerToggle(this, drawer, toolbar, openTitle, closeTitle)
    drawer.addDrawerListener(toggle)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)
    return toggle
}

/**
 *
 * @receiver Activity
 * @param toStart Class<T>
 * @param flags Int?
 */
@UiThread
fun <T : Activity> Activity.startActivity(toStart: Class<T>, flags: Int? = null) {
    startActivity(Intent(this, toStart).apply {
        if (flags != null) {
            this.flags = flags
        }
    })
}

/**
 *
 * @receiver Activity
 * @param toStart KClass<T>
 * @param flags Int?
 */
@UiThread
fun <T : Activity> Activity.startActivity(toStart: KClass<T>, flags: Int? = null) {
    startActivity(toStart.java, flags)
}

/**
 *
 * @receiver AppCompatActivity
 * @param toolbar Toolbar
 * @param actionToApply (ActionBar.() -> Unit)
 */
@UiThread
inline fun AppCompatActivity.setSupportActionBarAndApply(toolbar: Toolbar,
                                                         crossinline actionToApply: (ActionBar.() -> Unit)) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply(actionToApply)
}


/**
 * Pops all fragments from the current FragmentManager, except the bottom fragment
 * Logs if the operation fails (does not throw)
 * @receiver FragmentActivity
 */
@UiThread
fun FragmentActivity.popToFirstFragment() = runOnUiThread {
    supportFragmentManager?.popToFirstFragment()
}

@UiThread
fun FragmentActivity.replaceFragment(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager?.replaceFragment(container, fragment)
}

@UiThread
fun FragmentActivity.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager?.pushNewFragmentTo(container, fragment)
}

@UiThread
fun FragmentActivity.pushNewFragmentsTo(@IdRes container: Int, fragments: List<Fragment>) {
    supportFragmentManager?.pushNewFragmentsTo(container, fragments)
}

/**
 * Safe finish call, so wrapping it in runOnUiThread is not required.
 */
@AnyThread
fun Activity.safeFinish() = runOnUiThread(this::finish)

/**
 * starts the given intent and finishes this activity
 * @receiver Activity
 * @param intent Intent
 */
@AnyThread
fun Activity.startAndFinish(intent: Intent) = runOnUiAndFinish {
    startActivity(intent)
}

@AnyThread
fun Activity.startAndFinish(kClass: KClass<Activity>, flags: Int? = null) = runOnUiAndFinish {
    startActivity(kClass, flags)
}


@AnyThread
fun Activity.startAndFinish(jClass: Class<Activity>, flags: Int? = null) = runOnUiAndFinish {
    startActivity(jClass, flags)
}


/**
 *
 * @receiver Activity
 */
@Suppress("NOTHING_TO_INLINE")
@AnyThread
private inline fun Activity.runOnUiAndFinish(@UiThread crossinline action: EmptyFunction) = runOnUiThread {
    action()
    finish()
}

/**
 * The root view of an activity
 */
inline val Activity.rootView: View?
    @UiThread
    get() = window?.decorView?.rootView ?: findViewById(android.R.id.content)