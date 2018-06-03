package com.commonsense.android.kotlin.prebuilt.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.commonsense.android.kotlin.base.extensions.asUrl
import com.commonsense.android.kotlin.prebuilt.R
import com.commonsense.android.kotlin.prebuilt.activities.InbuiltWebView
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.system.logging.L

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

