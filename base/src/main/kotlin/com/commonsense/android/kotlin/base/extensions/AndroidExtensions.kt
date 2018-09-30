@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import android.text.*

/**
 * Specific android extensions.
 */

/**
 * converts an immutable string to an editable edition :)
 */
inline fun String.toEditable(): Editable =
        Editable.Factory.getInstance().newEditable(this)


