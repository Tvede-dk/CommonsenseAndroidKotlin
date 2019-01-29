package com.commonsense.android.kotlin.views.extensions

import android.widget.*
import androidx.annotation.*

/**
 * Created by Kasper Tvede on 04-12-2017.
 */

/**
 * Tells if this textview is empty (contains no text)
 */
val TextView.isEmpty: Boolean
    @UiThread
    get () = length() <= 0

/**
 *
 */
val TextView.isNotEmpty: Boolean
    @UiThread
    get () = !isEmpty

