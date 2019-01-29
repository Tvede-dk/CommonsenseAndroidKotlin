@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.view.*
import androidx.appcompat.widget.*

/**
 * Created by kasper on 06/06/2017.
 */
/**
 * Uses this menu item as a searchview iff possible.
 * @receiver MenuItem
 * @param action (SearchView) -> Unit this action gets called if the view was indeed a searchView
 */
inline fun MenuItem.useSearchView(crossinline action: (SearchView) -> Unit) {
    val item = actionView as? SearchView
    item?.let(action)
}
