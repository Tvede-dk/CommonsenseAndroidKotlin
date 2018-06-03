package com.commonsense.android.kotlin.system.extensions

import android.annotation.SuppressLint
import android.support.annotation.IdRes
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.commonsense.android.kotlin.system.logging.tryAndLog

/**
 * Created by Kasper Tvede on 20-05-2017.
 */


@UiThread
inline fun FragmentManager.transactionCommit(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commit()
}

@UiThread
inline fun FragmentManager.transactionCommitAllowStateLoss(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitAllowingStateLoss()
}

@SuppressLint("CommitTransaction") //AS failure,the commitnow is just as valid as commit
@UiThread
inline fun FragmentManager.transactionCommitNow(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitNow()
}

@SuppressLint("CommitTransaction") //AS failure,the commitnow is just as valid as commit
@UiThread
inline fun FragmentManager.transactionCommitNowAllowStateLoss(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitNowAllowingStateLoss()
}

/**
 * Pops all fragments from the current FragmentManager, except the bottom fragment
 * Logs if the operation fails (does not throw)
 */
@UiThread
fun FragmentManager.popToFirstFragment() {
    tryAndLog("popToFirstFragment failed.") {
        (0 until this.backStackEntryCount).forEach { this.popBackStack() }
        this.executePendingTransactions() //then allow Android to do the popping.
    }
}


@UiThread
fun FragmentManager.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) = transactionCommit {
    replace(container, fragment)
    addToBackStack(fragment.id.toString())
}

@UiThread
fun FragmentManager.replaceFragment(@IdRes container: Int, fragment: Fragment) = transactionCommitNow {
    replace(container, fragment)
}

@UiThread
fun FragmentManager.pushNewFragmentsTo(@IdRes container: Int, fragments: List<Fragment>) {
    fragments.forEach { it -> pushNewFragmentTo(container, it) }
}
