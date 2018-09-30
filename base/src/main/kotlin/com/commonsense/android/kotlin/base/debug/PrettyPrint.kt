@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.debug

import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*

/**
 *
 */

fun Map<String, String>.toPrettyString(name: String = "map"): String {
    return "$name content" + map { "${it.key.wrapInQuotes()} = ${it.value.wrapInQuotes()}  " }.prettyStringContent()
}

fun List<String>.prettyStringContent(ifNotEmpty: String = "", ifEmpty: String = "", prefix: String = "\n\t"): String =
        isNotEmpty().map(ifNotEmpty, ifEmpty) + prefix + joinToString("\n\t")