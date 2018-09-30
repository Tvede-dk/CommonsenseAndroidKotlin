@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.prebuilt.extensions

import android.content.*
import android.widget.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.prebuilt.*
import com.commonsense.android.kotlin.prebuilt.activities.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*

/**
 * Created by Kasper Tvede on 19-07-2017.
 */
/**
 * shows a given url in the browser. if for some reason the user have no browser, tries to present the homepage though the inbuilt browser in this lib.
 * if that fails, then a toast is shown.
 */
fun Context.startUrl(url: String, forceHttps: Boolean = true, useInbuiltBrowser: Boolean = true) {
    val safeUrl = url.asUrl(forceHttps)
    val toStart = Intent(Intent.ACTION_VIEW, safeUrl)
    try {
        startActivity(toStart)
    } catch (error: ActivityNotFoundException) {
        L.error("ContextExtensions", "Activity not found to launch url:$safeUrl", error)
        if (useInbuiltBrowser) {
            InbuiltWebView.showUri(safeUrl, this)
        } else {
            safeToast(R.string.missing_browser, Toast.LENGTH_SHORT)
        }
    }
}

