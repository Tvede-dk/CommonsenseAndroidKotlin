@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.features

import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.design.widget.*
import android.support.v4.app.*
import android.view.*
import com.commonsense.android.kotlin.base.*

/**
 * Created by kasper on 12/07/2017.
 */


fun Fragment.showSnackbar(view: View,
                          @StringRes text: Int,
                          @StringRes actionText: Int,
                          @IntRange(from = 0)
                          durationInMs: Int,
                          onAction: EmptyFunction,
                          modifyView: FunctionUnit<View>? = null,
                          onTimeout: EmptyFunction? = null,
                          onDismissOtherwise: EmptyFunction? = null): Snackbar {

    val mySnackbar = Snackbar.make(view,
            text, Snackbar.LENGTH_SHORT)
    mySnackbar.setAction(actionText) { onAction() }
    mySnackbar.duration = durationInMs
    mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            when {
                event == DISMISS_EVENT_TIMEOUT -> onTimeout?.invoke()
                event != DISMISS_EVENT_ACTION -> onDismissOtherwise?.invoke()
            }
        }
    })
    modifyView?.invoke(mySnackbar.view)
    mySnackbar.show()
    return mySnackbar
}


fun Fragment.showSnackbarNoAction(view: View,
                                  @StringRes text: Int,
                                  @IntRange(from = 0)
                                  durationInMs: Int,
                                  modifyView: FunctionUnit<View>? = null,
                                  onTimeout: EmptyFunction? = null,
                                  onDismissOtherwise: EmptyFunction? = null): Snackbar {

    val mySnackbar = Snackbar.make(view,
            text, Snackbar.LENGTH_SHORT)
    mySnackbar.duration = durationInMs
    mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            when {
                event == DISMISS_EVENT_TIMEOUT -> onTimeout?.invoke()
                event != DISMISS_EVENT_ACTION -> onDismissOtherwise?.invoke()
            }
        }
    })
    modifyView?.invoke(mySnackbar.view)
    mySnackbar.show()
    return mySnackbar
}
