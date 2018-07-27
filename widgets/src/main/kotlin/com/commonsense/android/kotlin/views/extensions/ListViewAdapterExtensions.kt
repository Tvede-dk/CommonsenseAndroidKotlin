package com.commonsense.android.kotlin.views.extensions

import android.widget.ArrayAdapter

/**
 * Created by Kasper Tvede on 1/27/2018.
 * Purpose:
 *
 */

/**
 * sets / replaces the content with the given content.
 */
fun <E> ArrayAdapter<E>.set(items: Collection<E>) {
    clear()
    addAll(items)
}
