@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import androidx.annotation.*
import androidx.fragment.app.*
import com.commonsense.android.kotlin.system.logging.*


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

@UiThread
inline fun FragmentManager.transactionCommitNow(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitNow()
}

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
        (0 until this.backStackEntryCount).forEach { _ -> this.popBackStack() }
        this.executePendingTransactions() //then allow Android to do the popping.
    }
}


/**
 * Pushes a new fragment onto the backstack and displays it.
 */
@UiThread
fun FragmentManager.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) = transactionCommit {
    replace(container, fragment)
    addToBackStack(fragment.id.toString())
}

/**
 * Replaces the current fragment with the given (in the container)
 */
@UiThread
fun FragmentManager.replaceFragment(@IdRes container: Int, fragment: Fragment) = transactionCommitNow {
    replace(container, fragment)
}

/**
 * appends multiple fragments to the given container
 */
@UiThread
fun FragmentManager.pushNewFragmentsTo(@IdRes container: Int, fragments: List<Fragment>) {
    fragments.forEach { pushNewFragmentTo(container, it) }
}
