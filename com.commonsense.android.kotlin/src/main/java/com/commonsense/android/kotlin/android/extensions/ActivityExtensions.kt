package com.commonsense.android.kotlin.android.extensions

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.StringRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.commonsense.kotlin.R
import java.io.File

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


inline fun AppCompatActivity.setSupportActionBarAndApply(toolbar: Toolbar, crossinline actionToApply: (ActionBar.() -> Unit)) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply(actionToApply)
}

fun Activity.openCamera() {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg")
    intent.putExtra(MediaStore.EXTRA_OUTPUT,
            Uri.fromFile(photo))
    if (takePictureIntent.resolveActivity(packageManager) != null) {
        startActivityForResult(takePictureIntent, 7896)//For testing.
    }
}