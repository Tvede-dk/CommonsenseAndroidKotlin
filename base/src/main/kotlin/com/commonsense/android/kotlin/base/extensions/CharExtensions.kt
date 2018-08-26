package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.extensions.collections.mapLazy
import kotlin.experimental.or

/**
 * Created by Kasper Tvede on 15-04-2018.
 * Purpose:
 *
 */


@Suppress("NOTHING_TO_INLINE")
inline fun Char.toCase(upperCase: Boolean): Char =
        upperCase.mapLazy(this::toUpperCase,
                this::toLowerCase)


//TODO in kotlin 1.3 use UByte
@Suppress("NOTHING_TO_INLINE")
inline fun hexCharsToByte(first: Char, second: Char): Short? {
    val firstToInt = first.mapFromHexValue()?.toShort() ?: return null
    val secondToInt = second.mapFromHexValue()?.toShort() ?: return null
    return firstToInt.shl(4) or secondToInt
}

@Suppress("NOTHING_TO_INLINE")
inline fun Char.mapFromHexValue(): Byte? {
    val zeroNum = '0'.toByte()
    val aNum = 'a'.toByte()
    val thisByte = toLowerCase().toByte()
    val mapped = isDigit().mapLazy({ thisByte - zeroNum }, { (thisByte - aNum) + 0xa }).toByte()
    //if this byte is outside the valid range after transformation then it would not have been valid before, thus its not a "0-9 - a -f" char.
    if (mapped < 0 || mapped > 0x0f) {
        return null
    }
    //its in range, return it.
    return mapped
}
