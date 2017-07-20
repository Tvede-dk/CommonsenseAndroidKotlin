package com.commonsense.android.kotlin.views.features

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog

/**
 * Created by Kasper Tvede on 21-07-2017.
 */

data class DialogOption(@StringRes val text: Int, val callback: () -> Unit)

fun Context.showOptionsDialog(@StringRes message: Int, vararg options: DialogOption) {

    val stringOptions = options.map { getString(it.text) }.toTypedArray()
    val dialog = AlertDialog.Builder(this).setItems(stringOptions,
            { dialogInterface: DialogInterface?, index: Int ->
                options[index].callback()
                dialogInterface?.dismiss()
            })
    dialog.show()

}

