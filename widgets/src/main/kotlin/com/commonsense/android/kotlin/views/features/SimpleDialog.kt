@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.features

import android.content.*
import android.support.annotation.*
import android.support.v7.app.*
import com.commonsense.android.kotlin.base.*

/**
 * Created by Kasper Tvede
 */

/**
 * A dialog option
 * @property text Int the text for this option
 * @property callback Function0<Unit> the action to invoke iff selected
 */
data class DialogOption(@StringRes val text: Int, val callback: EmptyFunction)

/**
 * Shows an options dialog, capable of having any number of items that can be selected
 * @receiver Context
 * @param title Int
 * @param optClosedOnOutside EmptyFunction?
 * @param options Array<out DialogOption>
 * @param configureDialog FunctionUnit<AlertDialog>? to customize the dialog even further.
 */
fun Context.showOptionsDialog(@StringRes title: Int,
                              optClosedOnOutside: EmptyFunction?,
                              vararg options: DialogOption,
                              configureDialog: FunctionUnit<AlertDialog>? = null) {
    val stringOptions = options.map { getString(it.text) }.toTypedArray()
    val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(stringOptions) { dialogInterface: DialogInterface?, index: Int ->
                options[index].callback()
                dialogInterface?.dismiss()
            }
    val dialogToShow: AlertDialog = dialog.create()
    dialogToShow.useOptionalCloseOnOutside(optClosedOnOutside)
    configureDialog?.invoke(dialogToShow)
    dialogToShow.show()
}

/**
 *
 * @receiver Context
 * @param title Int
 * @param optClosedOnOutside EmptyFunction?
 * @param layout Int
 * @param configureDialog FunctionUnit<AlertDialog>? to customize the dialog even further.
 */
fun Context.showCustomDialog(@StringRes title: Int,
                             optClosedOnOutside: EmptyFunction?,
                             @LayoutRes layout: Int,
                             configureDialog: FunctionUnit<AlertDialog>? = null) {
    val dialogToShow = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(layout).create()

    dialogToShow.useOptionalCloseOnOutside(optClosedOnOutside)
    configureDialog?.invoke(dialogToShow)
    dialogToShow.show()

}

/**
 * if given null, then cancel on touches outside becomes false, if not null, then it does, and that function will be called.
 * @receiver AlertDialog
 * @param optClosedOnOutside EmptyFunction?
 */
internal fun AlertDialog.useOptionalCloseOnOutside(optClosedOnOutside: EmptyFunction?) {
    if (optClosedOnOutside != null) {
        setCanceledOnTouchOutside(true)
        setOnCancelListener { optClosedOnOutside() }
    } else {
        setCanceledOnTouchOutside(false)
    }
}