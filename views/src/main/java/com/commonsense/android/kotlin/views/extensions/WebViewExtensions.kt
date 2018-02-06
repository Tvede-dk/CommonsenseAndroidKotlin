package com.commonsense.android.kotlin.views.extensions

import android.net.Uri
import android.webkit.WebView

/**
 * Created by Kasper Tvede on 21-05-2017.
 */

private const val textHtmlWithCharset = "text/html;charset=UTF-8"

/**
 * Loads the given html as an html document, with a default utf8 encoding (since java uses that for strings as well).
 */
fun WebView.loadHtml(htmlCode: String) = this.loadData(htmlCode, textHtmlWithCharset, null)

fun WebView.loadHtmlWithBaseURL(baseUrl: String?, htmlCode: String) {
    if (baseUrl != null) {
        this.loadDataWithBaseURL(baseUrl, htmlCode, textHtmlWithCharset, null, null)
    } else {
        loadHtml(htmlCode)
    }
}

/**
 * Loads the web page at the given uri
 */
fun WebView.loadUri(uri: Uri) {
    loadUrl(uri.toString())
}