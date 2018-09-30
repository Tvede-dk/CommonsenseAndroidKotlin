@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import android.net.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.base.extensions.generic.*

/**
 * Created by Kasper Tvede on 23-07-2017.
 */

const val httpsPrefix = "https://"
const val httpPrefix = "http://"

/**
 * Makes sure that the given string would work as a url.
 * this means prefixing it with (http(s)://) if missing
 */
inline fun String.asUrl(forceHttps: Boolean = true): Uri {
    val isHttp = startsWith(httpPrefix)
    val isHttps = startsWith(httpsPrefix)

    //make sure the url starts with https, or if the user really wants to be insecure, http.
    val safeUrl = when {
        !isHttp && !isHttps -> httpsPrefix + this
        isHttp && forceHttps -> this.replace(httpPrefix, httpsPrefix) //replace http with https
        isHttp && !forceHttps -> this
        isHttps -> this
        else -> this
    }
    return Uri.parse(safeUrl)
}


inline fun Uri.fileExtension(): String? {
    return path?.fileExtension()
}

inline fun String.fileExtension(): String? {
    return lastIndexOf('.').let {
        if (it > 0 && it + 1 < length) {
            return substring(it + 1)
        } else {
            null
        }
    }
}

/**
 * Removes the query from a Uri.
 *
 */
inline fun Uri.withoutQueryParameters(): Uri {
    return buildUpon().query("").build()
}


inline fun String.wrapInQuotes(): String = "\"${this}\""


/**
 * The opposite of ByteArray.toHexString , so takes a hex string (eg "0x20") and converts it to a byte array of that
 * @receiver String
 * @return ByteArray
 */
inline fun String.fromHexStringToByteArray(): ShortArray? {
    //strip prefix iff asked to
    if (length.isOdd || isEmpty()) {
        return null
    }
    //we have the hex prefix iff it starts with "0x". strip that iff necessary
    val string = skipStartsWith("0x", true)
    val result = ShortArray(string.length / 2)
    string.foreach2Indexed { index: Int, first: Char, second: Char ->
        val shortValue = hexCharsToByte(first, second) ?: return@fromHexStringToByteArray null
        result[index / 2] = shortValue
    }
    return result
}


/**
 * Skips the given part if it starts with it.
 * @receiver String
 * @param prefix String the prefix we are looking for (and the part that will be skipped iff there.
 * @param ignoreCase Boolean how we should compare prefix with this string
 * @return String the resulting string, either the original or substring by the prefix length
 */
inline fun String.skipStartsWith(prefix: String, ignoreCase: Boolean = false): String {
    val startsWith = startsWith(prefix, ignoreCase)
    return startsWith.mapLazy(ifTrue = { substring(prefix.length) }, ifFalse = { this })
}

/**
 *
 * @receiver String
 * @param action (first: Char, second: Char) -> Unit
 */
inline fun String.foreach2(action: Function2Unit<Char, Char>) =
        GenericExtensions.forEach2(length, this::get, action)

/**
 *
 * @receiver String
 * @param action (first: Char, second: Char) -> Unit
 */
inline fun String.foreach2Indexed(action: Function2IndexedUnit<Char, Char>) =
        GenericExtensions.forEach2Indexed(length, this::get, action)

