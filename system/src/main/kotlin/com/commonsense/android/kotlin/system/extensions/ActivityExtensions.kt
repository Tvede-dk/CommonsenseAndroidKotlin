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
import kotlin.reflect.KClass


fun AppCompatActivity.setupToolbarAppDrawer(drawer: DrawerLayout, toolbar: Toolbar, @StringRes openTitle: Int, @StringRes closeTitle: Int): ActionBarDrawerToggle {
    setSupportActionBar(toolbar)
    val toggle = ActionBarDrawerToggle(this, drawer, toolbar, openTitle, closeTitle)
    drawer.addDrawerListener(toggle)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)
    return toggle
}

fun <T : Activity> Activity.startActivity(toStart: Class<T>) {
    startActivity(Intent(this, toStart))
}

fun <T : Activity> Activity.startActivity(toStart: KClass<T>) {
    startActivity(Intent(this, toStart.java))
}


inline fun AppCompatActivity.setSupportActionBarAndApply(toolbar: Toolbar, crossinline actionToApply: (ActionBar.() -> Unit)) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply(actionToApply)
}

/**
 * Pops all fragments from the current FragmentManager, except the bottom fragment
 * Logs if the operation fails (does not throw)
 */
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

@AnyThread
fun Activity.safeFinish() = runOnUiThread(this::finish)
