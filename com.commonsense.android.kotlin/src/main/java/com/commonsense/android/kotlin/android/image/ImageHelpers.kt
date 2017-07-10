package com.commonsense.android.kotlin.android.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import map

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * want to be able to load a "scaled bitmap"
 */
fun Uri.loadBitmapScaled(contentResolver: ContentResolver, width: Int): Bitmap? {
    var input = contentResolver.openInputStream(this)
    val onlyBoundsOptions = BitmapFactory.Options()
    try {

        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
    } finally {
        input.close()
    }

    if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
        return null
    }

    val originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
            .map(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth)

    val ratio = (originalSize > width).map(originalSize / width.toDouble(), 1.0)

    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
    bitmapOptions.inDither = true //optional
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//
    input = contentResolver.openInputStream(this)
    return try {
        BitmapFactory.decodeStream(input, null, bitmapOptions)
    } finally {
        input.close()
    }
}


private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
    val k = Integer.highestOneBit(Math.floor(ratio).toInt())
    return (k == 0).map(1, k)
}

/**
 * want to be able to pre-scale an image to various sizes
 */

/**
 * want to be able to create previews of a quality scaling (eg, at 85 % , 90%, and 95% in jpg).
 */

/**
 * more bitmap fun ?
 */