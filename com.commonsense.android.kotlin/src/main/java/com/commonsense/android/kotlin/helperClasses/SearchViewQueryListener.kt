package com.commonsense.android.kotlin.helperClasses

import android.support.v7.widget.SearchView

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