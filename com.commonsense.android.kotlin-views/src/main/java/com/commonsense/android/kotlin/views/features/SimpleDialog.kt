package com.commonsense.android.kotlin.views.features

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import com.commonsense.android.kotlin.base.EmptyFunction

/**
 * Created by Kasper Tvede on 21-07-2017.
 */

data class DialogOption(@StringRes val text: Int, val callback: EmptyFunction)

fun Context.showOptionsDialog(@StringRes title: Int, optClosedOnOutside: EmptyFunction?, vararg options: DialogOption) {

    val stringOptions = options.map { getString(it.text) }.toTypedArray()
    val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(stringOptions, { dialogInterface: DialogInterface?, index: Int ->
                options[index].callback()
                dialogInterface?.dismiss()
            })


    val dialogToShow = dialog.create()
    if (optClosedOnOutside != null) {
        dialogToShow.setCanceledOnTouchOutside(true)
        dialogToShow.setOnCancelListener({ optClosedOnOutside() })
    } else {
        dialogToShow.setCanceledOnTouchOutside(false)
    }

    dialogToShow.show()

}

