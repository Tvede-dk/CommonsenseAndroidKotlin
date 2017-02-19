package com.commonsense.android.kotlin.android.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.annotation.StringRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.commonsense.android.kotlin.android.logging.L
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

fun Activity.startUrl(url: String, forceHttps: Boolean = true, useInbuildBrowser: Boolean = true) {
    val isHttp = url.startsWith("http://")
    val isHttps = url.startsWith("https://")

    val safeUrl = when {
        !isHttp && !isHttps -> "https://" + url
        isHttp && forceHttps -> url.replace("http://", "https://")
        isHttp && !forceHttps -> url
        isHttps -> url
        else -> url
    }

    val toStart = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl))
    try {
        startActivity(toStart)
    } catch (notFound: ActivityNotFoundException) {
        L.error("ActivityExtensions", "Activity not found to launch url:$safeUrl", notFound)
        if (useInbuildBrowser) {

        } else {
            safeToast(R.string.missing_browser, Toast.LENGTH_SHORT)
        }
        //TODO present inbuild webview.
    }
}


//TODO make annotations for length
fun Activity.safeToast(@StringRes message: Int, length: Int) {
    if (isVisible) {

    }
}

val Activity.isVisible: Boolean
    get() = false
