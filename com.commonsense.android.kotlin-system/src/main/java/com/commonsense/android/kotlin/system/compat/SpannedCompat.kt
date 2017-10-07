package com.commonsense.android.kotlin.system.compat

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import com.commonsense.android.kotlin.system.extensions.isApiOverOrEqualTo


/**
 * Created by kasper on 26/09/2017.
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
fun String.fromHtml(): Spanned {
    (return if (isApiOverOrEqualTo(24)) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    })
}