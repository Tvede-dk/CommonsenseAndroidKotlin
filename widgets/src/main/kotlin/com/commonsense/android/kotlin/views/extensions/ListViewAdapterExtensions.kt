package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.*
import android.widget.*


/**
 * Sets / replaces the content with the given content.
 * @receiver ArrayAdapter<E>
 * @param items Collection<E> the items to set the adapter to
 */
@UiThread
@Suppress("NOTHING_TO_INLINE")
inline fun <E> ArrayAdapter<E>.set(items: Collection<E>) {
    clear()
    addAll(items)
}
