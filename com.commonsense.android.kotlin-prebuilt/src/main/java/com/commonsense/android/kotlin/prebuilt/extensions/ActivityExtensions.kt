package com.commonsense.android.kotlin.prebuilt.extensions

import android.app.Activity
import android.app.ProgressDialog
import com.commonsense.android.kotlin.system.R

/**
 * Created by Kasper Tvede on 19-07-2017.
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