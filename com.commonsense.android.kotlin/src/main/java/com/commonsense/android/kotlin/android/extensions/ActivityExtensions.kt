package com.commonsense.android.kotlin.android.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.commonsense.kotlin.R

/**
 * Created by Kasper Tvede on 30-10-2016.
 */


fun Activity.createSpinnerDialog(): ProgressDialog {
    val progress = ProgressDialog(this)
    with(progress) {
        setTitle(R.string.please_wait)
        isIndeterminate = true
        setCancelable(false)
    }
    return progress
}

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
