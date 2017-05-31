package com.commonsense.android.kotlin.android.extensions

/**
 * Created by Kasper Tvede on 30-05-2017.
 */


fun isApiOverOrEqualTo(level: Int): Boolean {
    return getSdkLevel() >= level
}

fun isApiOver(level: Int): Boolean {
    return getSdkLevel() > level
}

fun isApiLowerThan(level: Int): Boolean {
    return getSdkLevel() < level
}

fun getSdkLevel(): Int {
    return android.os.Build.VERSION.SDK_INT
}