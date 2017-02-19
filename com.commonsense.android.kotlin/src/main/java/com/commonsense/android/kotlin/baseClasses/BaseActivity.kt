package com.commonsense.android.kotlin.baseClasses

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.commonsense.android.kotlin.android.PermissionsHandling

/**
 * Created by admin on 29-09-2016.
 */

open class BaseActivity : AppCompatActivity() {

    val PermissionHandler by lazy {
        PermissionsHandling()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHandler.onRequestPermissionResult(requestCode, permissions, grantResults)
    }


}

fun Activity.postFinish() {
    runOnUiThread { finish() }
}

fun FragmentActivity.replaceFragment(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(container, fragment).commitNow()
}

fun FragmentActivity.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(container, fragment).addToBackStack(fragment.id.toString()).commit()
}

