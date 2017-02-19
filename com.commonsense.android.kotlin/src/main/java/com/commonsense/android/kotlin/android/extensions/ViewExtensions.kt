package com.commonsense.android.kotlin.android.extensions

import android.view.View

/**
 * Created by Kasper Tvede on 29-10-2016.
 */
inline fun View.setOnClick(crossinline listener: () -> Unit) {
    setOnClickListener { listener() }
}

inline fun View.setOnClickView(crossinline listener: (View) -> Unit) {
    setOnClickListener { listener(it) }
}
