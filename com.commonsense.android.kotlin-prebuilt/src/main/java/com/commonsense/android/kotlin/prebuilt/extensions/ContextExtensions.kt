package com.commonsense.android.kotlin.prebuilt.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.commonsense.android.kotlin.prebuilt.activities.InbuiltWebview
import com.commonsense.android.kotlin.system.R
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.system.logging.L

/**
 * Created by Kasper Tvede on 19-07-2017.
 */


fun Context.startUrl(url: String, forceHttps: Boolean = true, useInbuildBrowser: Boolean = true) {
    val isHttp = url.startsWith("http://")
    val isHttps = url.startsWith("https://")

    val safeUrl = when {
        !isHttp && !isHttps -> "https://" + url
        isHttp && forceHttps -> url.replace("http://", "https://")
        isHttp && !forceHttps -> url
        isHttps -> url
        else -> url
    }

    val toStart = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl))
    try {
        startActivity(toStart)
    } catch (notFound: ActivityNotFoundException) {
        L.error("ActivityExtensions", "Activity not found to launch url:$safeUrl", notFound)
        if (useInbuildBrowser) {
            InbuiltWebview.showUrl(safeUrl, this)
        } else {
            safeToast(R.string.missing_browser, Toast.LENGTH_SHORT)
        }
    }
}
