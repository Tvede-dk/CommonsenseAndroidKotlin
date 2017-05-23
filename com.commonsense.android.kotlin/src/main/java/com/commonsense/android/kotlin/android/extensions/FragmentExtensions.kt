package com.commonsense.android.kotlin.android.extensions

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.WindowManager

/**
 * Created by Kasper Tvede on 10-01-2017.
 */


fun Fragment.getParrentContainerId() = (view?.parent as? ViewGroup)?.id

fun Fragment.PresentDialer(phoneNumber: String) {
    context.PresentDialer(phoneNumber)
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