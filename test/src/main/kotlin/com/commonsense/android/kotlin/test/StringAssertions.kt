package com.commonsense.android.kotlin.test

import org.junit.Assert

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 *
 */

fun String.assertContains(value: String,
                          ignoreCase: Boolean = false,
                          message: String = "Could not find \"$value\", in  \r\n\"$this\"") {
    Assert.assertTrue(message, this.contains(value, ignoreCase = ignoreCase))
}

fun String.assertContainsNot(value: String,
                             ignoreCase: Boolean = false,
                             message: String = "") {
    Assert.assertFalse("$message \n Reason: Could find \"$value\", in  \r\n\"$this\"", this.contains(value, ignoreCase = ignoreCase))
}

fun String.assert(value: String, message: String = "") {
    Assert.assertEquals(message, value, this)
}


fun String.assertNot(value: String, message: String = "") {
    Assert.assertNotEquals(message, value, this)
}


fun String.assertContainsInOrder(values: List<String>,
                                 ignoreCase: Boolean, message: String = "") {
    var currentIndex = 0
    values.forEach {
        val next = indexOf(it, currentIndex, ignoreCase)
        if (next < 0) {
            failTest("could not find \n\t\"$it\" after index $currentIndex in string \n" +
                    "\"$this\"\n" +
                    "\tafter index is :\"${this.substring(currentIndex)}\"")
            return
        }
        currentIndex = next + it.length
    }
}
fun String.assertStartsWith(prefix: String,
                            ignoreCase: Boolean = false,
                            message: String = "") {
    val textOutput = "$message \n Could not find \"$prefix\", in  \n" +
            "\"$this\""
    Assert.assertTrue(textOutput, this.startsWith(prefix, ignoreCase = ignoreCase))
}

