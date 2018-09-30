@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions


inline infix fun Short.shl(shift: Int): Short = (this.toInt() shl shift).toShort()

inline infix fun Short.shr(shift: Int): Short = (this.toInt() shr shift).toShort()
