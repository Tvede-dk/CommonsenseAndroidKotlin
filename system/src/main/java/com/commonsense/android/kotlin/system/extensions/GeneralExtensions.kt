package com.commonsense.android.kotlin.system.extensions

/**
 * Created by Kasper Tvede on 30-05-2017.
 */


inline fun isApiOverOrEqualTo(level: Int): Boolean {
    return getSdkLevel() >= level
}

inline fun isApiOver(level: Int): Boolean {
    return getSdkLevel() > level
}

inline fun isApiLowerThan(level: Int): Boolean {
    return getSdkLevel() < level
}

inline fun getSdkLevel(): Int {
    return android.os.Build.VERSION.SDK_INT
}