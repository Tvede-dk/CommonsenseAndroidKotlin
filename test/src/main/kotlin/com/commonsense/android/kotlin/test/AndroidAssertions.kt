package com.commonsense.android.kotlin.test

import android.net.*
import org.junit.*

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 *
 */


fun Uri.assert(value: Uri, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Uri.assert(value: String, message: String = "") {
    Assert.assertEquals(message, value, this.toString())
}
