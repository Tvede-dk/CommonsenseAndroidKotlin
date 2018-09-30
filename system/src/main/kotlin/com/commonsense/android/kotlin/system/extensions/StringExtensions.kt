package com.commonsense.android.kotlin.system.extensions

import java.net.*

/**
 * Created by Kasper Tvede on 28-05-2018.
 * Purpose:
 *
 */
fun String.urlEncoded(): String {
    return URLEncoder.encode(this, "UTF-8")
}