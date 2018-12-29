@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.systemIntegration

import android.content.*
import android.net.*
import com.commonsense.android.kotlin.system.extensions.*

class ChooserIntent(val title: CharSequence)

inline fun ChooserIntent?.getIntent(otherIntent: Intent): Intent {
    return if (this == null) {
        otherIntent
    } else {
        Intent.createChooser(otherIntent, this.title)
    }
}

inline fun Context.showOnMaps(address: String, useChooser: ChooserIntent? = null) {
    val urlEncoded = address.urlEncoded()
    val toLaunch = Uri.parse("geo:0,0?q=$urlEncoded")
    val intent = Intent(Intent.ACTION_VIEW, toLaunch)
    startActivitySafe(useChooser.getIntent(intent))
}


inline fun Context.presentDialer(phoneNumber: String, useChooser: ChooserIntent? = null) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivitySafe(useChooser.getIntent(intent))
}


inline fun Context.openMail(emailTo: String,
                            potentialSubject: String,
                            potentialEmailMessage: String,
                            useChooser: ChooserIntent? = null) {

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
        putExtra(Intent.EXTRA_SUBJECT, potentialSubject)
        putExtra(Intent.EXTRA_TEXT, potentialEmailMessage)
        putExtra(Intent.EXTRA_HTML_TEXT, potentialEmailMessage)
    }
    startActivitySafe(useChooser.getIntent(intent))
}