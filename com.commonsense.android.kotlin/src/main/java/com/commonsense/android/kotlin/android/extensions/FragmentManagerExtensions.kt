package com.commonsense.android.kotlin.android.extensions

import android.annotation.SuppressLint
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by Kasper Tvede on 20-05-2017.
 */


inline fun FragmentManager.transactionCommit(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commit()
}

inline fun FragmentManager.transactionCommitAllowStateLoss(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitAllowingStateLoss()
}

@SuppressLint("CommitTransaction") //AS failure,the commitnow is just as valid as commit
inline fun FragmentManager.transactionCommitNow(crossinline action: (FragmentTransaction.() -> Unit)){
    beginTransaction().apply(action).commitNow()
}

@SuppressLint("CommitTransaction") //AS failure,the commitnow is just as valid as commit
inline fun FragmentManager.transactionCommitNowAllowStateLoss(crossinline action: (FragmentTransaction.() -> Unit)) {
    beginTransaction().apply(action).commitNowAllowingStateLoss()
}