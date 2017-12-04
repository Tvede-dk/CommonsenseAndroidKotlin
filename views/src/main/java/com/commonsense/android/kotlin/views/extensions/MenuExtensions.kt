package com.commonsense.android.kotlin.views.extensions

import android.support.v7.widget.SearchView
import android.view.MenuItem

/**
 * Created by kasper on 06/06/2017.
 */
inline fun MenuItem.useSearchView(crossinline action: (SearchView) -> Unit) {
    val item = actionView as? SearchView
    item?.let(action)
}
