package com.commonsense.android.kotlin.system.extensions

import android.app.Activity
import android.support.v4.app.Fragment

import android.view.MenuItem
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue

/**
 * Created by kasper on 28/08/2017.
 */

fun MenuItem?.backPressIfHome(activity: Activity): Boolean {
    return this?.backPressIfHome(activity::onBackPressed) ?: false
}

fun MenuItem?.backPressIfHome(fragment: Fragment): Boolean {
    return this?.backPressIfHome(fragment::onBackPressed) ?: false
}

fun MenuItem?.backPressIfHome(@Suppress("DEPRECATION")
                              fragment: android.app.Fragment): Boolean {
    return this?.backPressIfHome(fragment::onBackPressed) ?: false
}

private fun MenuItem.backPressIfHome(action: EmptyFunction): Boolean {
    return (this.itemId == android.R.id.home).ifTrue(action)
}