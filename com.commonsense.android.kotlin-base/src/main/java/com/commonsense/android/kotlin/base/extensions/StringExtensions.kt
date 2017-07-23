package com.commonsense.android.kotlin.base.extensions

import android.net.Uri

/**
 * Created by Kasper Tvede on 23-07-2017.
 */

private val httpsPrefix = "https://"
private val httpPrefix = "http://"

/**
 * Makes sure that the given string would work as a url.
 * this means prefixing it with (http(s)://) if missing
 */
fun String.asUrl(forceHttps: Boolean = true): Uri {
    val isHttp = startsWith(httpPrefix)
    val isHttps = startsWith(httpsPrefix)

    //make sure the url starts with https, or if the user really wants to be insecure, http.
    val safeUrl = when {
        !isHttp && !isHttps -> httpsPrefix + this
        isHttp && forceHttps -> this.replace(httpPrefix, httpsPrefix) //replace http with https
        isHttp && !forceHttps -> this
        isHttps -> this
        else -> this
    }
    return Uri.parse(safeUrl)
}