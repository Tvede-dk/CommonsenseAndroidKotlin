package com.commonsense.android.kotlin.system.extensions

import android.app.Activity
import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlin.reflect.KClass

/**
 * Created by Kasper Tvede on 30-10-2016.
 */


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
