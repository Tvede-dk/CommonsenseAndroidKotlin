@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.reflection

import java.lang.reflect.*


/**
 *
 */
@Throws(Exception::class)
inline fun <reified T> Field.getAs(obj: Any): T? {
    return get(obj) as T?
}