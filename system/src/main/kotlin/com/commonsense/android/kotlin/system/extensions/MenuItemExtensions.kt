@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.app.*
import android.view.*
import androidx.fragment.app.Fragment
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*

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