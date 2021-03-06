@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.features

import android.support.v7.widget.*

/**
 * Created by kasper on 06/06/2017.
 */
typealias SearchViewListener = (String?) -> Unit

class SearchViewQueryListener(val callback: SearchViewListener) : SearchView.OnQueryTextListener {


    override fun onQueryTextSubmit(query: String?): Boolean {
        callback(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        callback(newText)
        return false
    }

}