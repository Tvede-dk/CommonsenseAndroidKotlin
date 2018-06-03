package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.extensions.collections.mapLazy

/**
 * Created by Kasper Tvede on 15-04-2018.
 * Purpose:
 *
 */


@Suppress("NOTHING_TO_INLINE")
inline fun Char.toCase(upperCase: Boolean): Char =
        upperCase.mapLazy(this::toUpperCase,
                this::toLowerCase)
