package com.commonsense.android.kotlin.android.extensions

import android.webkit.WebView

/**
 * Created by Kasper Tvede on 21-05-2017.
 */


/**
 * Loads the given html as an html document, with a default utf8 encoding (since java uses that for strings as well).
 */
fun WebView.loadHtml(htmlCode: String) = this.loadData(htmlCode, "text/html;charset=UTF-8", null)
