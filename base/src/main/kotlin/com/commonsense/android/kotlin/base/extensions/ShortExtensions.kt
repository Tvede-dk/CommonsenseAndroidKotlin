package com.commonsense.android.kotlin.base.extensions


@Suppress("NOTHING_TO_INLINE")
inline infix fun Short.shl(shift: Int): Short = (this.toInt() shl shift).toShort()

@Suppress("NOTHING_TO_INLINE")
inline infix fun Short.shr(shift: Int): Short = (this.toInt() shr shift).toShort()
