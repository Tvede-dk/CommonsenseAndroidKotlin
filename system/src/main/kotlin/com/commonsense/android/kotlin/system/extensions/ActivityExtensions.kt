package com.commonsense.android.kotlin.system.extensions

import android.app.Activity
import android.content.Intent
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import kotlin.reflect.KClass


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

@UiThread
fun <T : Activity> Activity.startActivity(toStart: Class<T>) {
    startActivity(Intent(this, toStart))
}

@UiThread
fun <T : Activity> Activity.startActivity(toStart: KClass<T>) {
    startActivity(Intent(this, toStart.java))
}


@UiThread
inline fun AppCompatActivity.setSupportActionBarAndApply(toolbar: Toolbar,
                                                         crossinline actionToApply: (ActionBar.() -> Unit)) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply(actionToApply)
}

/**
 * Pops all fragments from the current FragmentManager, except the bottom fragment
 * Logs if the operation fails (does not throw)
 */
@AnyThread
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


inline val Activity.rootView: View?
    get() = window?.decorView?.rootView ?: findViewById(android.R.id.content)