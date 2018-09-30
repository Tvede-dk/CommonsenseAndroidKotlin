@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.support.v4.app.*
import android.support.v7.app.*
import android.view.*

/**
 * Created by Kasper Tvede on 10-01-2017.
 */


fun Fragment.getParrentContainerId() = (view?.parent as? ViewGroup)?.id

fun Fragment.presentDialer(phoneNumber: String) {
    context?.presentDialer(phoneNumber)
}

fun Fragment.getActivityAsCompat(): AppCompatActivity? {
    return activity as? AppCompatActivity
}

fun DialogFragment.dialogFillParentView() {
    // Get existing layout params for the window
    val params = dialog.window?.attributes
    // Assign window properties to fill the parent
    params?.width = WindowManager.LayoutParams.MATCH_PARENT
    params?.height = WindowManager.LayoutParams.MATCH_PARENT
    dialog.window?.attributes = params as android.view.WindowManager.LayoutParams
    // Call super onResume after sizing
}

fun Fragment.onBackPressed() {
    activity?.onBackPressed()
}


@Suppress("DEPRECATION")
fun android.app.Fragment.onBackPressed() {
    activity?.onBackPressed()
}

/**
 * Pops all fragments from the current FragmentManager, except the bottom fragment
 * Logs if the operation fails (does not throw)
 */
fun Fragment.popToFirstFragment() {
    activity?.popToFirstFragment()
}


fun Fragment.finishActivity() {
    activity?.finish()
}