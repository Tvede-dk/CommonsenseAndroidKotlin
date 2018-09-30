package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.*
import android.widget.*

/**
 * Created by Kasper Tvede on 04-12-2017.
 */

/**
 * Tells if this textview is empty (contains no text)
 */
val TextView.isEmpty: Boolean
    @UiThread
    get () = length() <= 0