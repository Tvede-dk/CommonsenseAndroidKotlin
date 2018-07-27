package com.commonsense.android.kotlin.base.extensions.reflection

import java.lang.reflect.*


/**
 *
 */
inline fun <reified T> Field.getAs(obj: Any): T? {
    return get(obj) as T?
}