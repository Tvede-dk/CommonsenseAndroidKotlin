package com.commonsense.android.kotlin.android.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import map

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * want to be able to load a "scaled bitmap"
 */
suspend fun Uri.loadBitmapScaled(contentResolver: ContentResolver, width: Int): Bitmap? {
    var result: Bitmap? = null
    launch(CommonPool) {
        val onlyBoundsOptions = BitmapFactory.Options()
        contentResolver.openInputStream(this@loadBitmapScaled).use { input ->
            onlyBoundsOptions.inJustDecodeBounds = true
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        }

        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return@launch
        }

        val originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
                .map(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth)

        val ratio = (originalSize > width).map(originalSize / width.toDouble(), 1.0)
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//

        contentResolver.openInputStream(this@loadBitmapScaled).use { inputToDecode ->
            result = BitmapFactory.decodeStream(inputToDecode, null, bitmapOptions)
        }
        return@launch
    }.join()
    return result
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