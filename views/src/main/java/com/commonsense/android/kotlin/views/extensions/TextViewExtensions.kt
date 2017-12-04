package com.commonsense.android.kotlin.views.extensions

import android.widget.TextView

/**
 * Created by Kasper Tvede on 04-12-2017.
 */

val TextView.isEmpty: Boolean
    get () = length() <= 0