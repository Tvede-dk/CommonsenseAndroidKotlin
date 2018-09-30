package com.commonsense.android.kotlin.system.extensions

import android.content.*

/**
 * Created by Kasper Tvede on 20-05-2017.
 */


inline fun SharedPreferences.editWith(crossinline action: (SharedPreferences.Editor.() -> Unit)) {
    edit().apply(action).apply()
}