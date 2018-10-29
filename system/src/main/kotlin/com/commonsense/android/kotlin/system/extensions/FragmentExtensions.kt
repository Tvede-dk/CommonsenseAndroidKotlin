@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.os.*
import android.support.v4.app.*
import android.support.v7.app.*
import android.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import kotlinx.coroutines.experimental.Runnable

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

/**
 * Calls the onback presed for the parent activity
 * @receiver Fragment
 */
fun Fragment.onBackPressed() {
    activity?.onBackPressed()
}

/**
 * Calls the onback presed for the parent activity
 * @receiver android.app.Fragment
 */
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

/**
 * Finishes the activity (safely)
 * @receiver Fragment
 */
fun Fragment.finishActivity() {
    activity?.safeFinish()
}

/**
 * Removes this fragment immediately.
 * @receiver Fragment
 */
fun Fragment.popThisFragment() {
    activity?.supportFragmentManager?.performActionAfterPendingTransactions {
        transactionCommitNowAllowStateLoss {
            remove(this@popThisFragment)
        }
    }
}

/**
 * Warning this can take "quite" some time due to animations ect.
 * @receiver FragmentManager
 */
fun FragmentManager.performActionAfterPendingTransactions(action: EmptyReceiver<FragmentManager>) {
    val handler = Handler(Looper.getMainLooper())
    val runner: Runnable = AfterPendingTransactionsRunner(this, handler::post, action)
    handler.post(runner)
}

/**
 *
 * @property fragmentManager FragmentManager
 * @property rerun Function1<Runnable, Boolean>
 * @property action [@kotlin.ExtensionFunctionType] Function1<FragmentManager, Unit>
 * @constructor
 */
private class AfterPendingTransactionsRunner(
        val fragmentManager: FragmentManager,
        val rerun: Function1<Runnable, Boolean>,
        val action: EmptyReceiver<FragmentManager>) : java.lang.Runnable {
    override fun run() {
        var didWork = false
        try {
            fragmentManager.executePendingTransactions()
            didWork = true
        } catch (e: Throwable) {
            rerun(this)
            //reschedule
        }
        didWork.ifTrue { action(fragmentManager) }
    }

}