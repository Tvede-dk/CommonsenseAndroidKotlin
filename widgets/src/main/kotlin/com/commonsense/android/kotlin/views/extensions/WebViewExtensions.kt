@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.net.*
import android.webkit.*

/**
 * Created by Kasper Tvede on 21-05-2017.
 */
/**
 * The Default encoding and type for a given html page.
 */
private const val textHtmlWithCharset = "text/html;charset=UTF-8"


/**
 * Loads the given html as an html document, with a default utf8 encoding (since java uses that for strings as well).
 * @receiver WebView the
 * @param htmlCode String the html code we are going to load.
 */
fun WebView.loadHtml(htmlCode: String) = this.loadData(htmlCode, textHtmlWithCharset, null)

/**
 * wrapper around the underlying functions but with sane defaults.
 *
 * @receiver WebView
 * @param baseUrl String? the potential base url to load the webview with (start address )
 * @param htmlCode String the html to load into it.
 */
fun WebView.loadHtmlWithBaseURL(baseUrl: String?, htmlCode: String) {
    if (baseUrl != null) {
        this.loadDataWithBaseURL(baseUrl, htmlCode, textHtmlWithCharset, null, null)
    } else {
        loadHtml(htmlCode)
    }
}


/**
 * Loads the web page at the given uri
 * @receiver WebView
 * @param uri Uri the Uri to load
 */
fun WebView.loadUri(uri: Uri) {
    loadUrl(uri.toString())
}