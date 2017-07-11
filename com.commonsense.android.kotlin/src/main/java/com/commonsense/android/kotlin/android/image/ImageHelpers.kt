package com.commonsense.android.kotlin.android.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.annotation.IntRange
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import map
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * want to be able to load a "scaled bitmap"
 */
suspend fun Uri.loadBitmapScaled(contentResolver: ContentResolver, width: Int): Deferred<Bitmap?> = async(CommonPool) {
    val onlyBoundsOptions = this@loadBitmapScaled.loadBitmapSize(contentResolver).await()
            ?: return@async null

    val originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
            .map(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth)

    val ratio = (originalSize > width).map(originalSize / width.toDouble(), 1.0)

    return@async loadBitmapWithsampleSize(contentResolver, ratio).await()

}

suspend fun Uri.loadBitmapWithsampleSize(contentResolver: ContentResolver, ratio: Double): Deferred<Bitmap?> = async(CommonPool) {
    val bitmapOptions = BitmapFactory.Options().apply {
        @IntRange(from = 1)
        inSampleSize = getPowerOfTwoForSampleRatio(ratio)
        inPreferredConfig = Bitmap.Config.ARGB_8888//

    }

    contentResolver.openInputStream(this@loadBitmapWithsampleSize).use { inputToDecode ->
        return@async BitmapFactory.decodeStream(inputToDecode, null, bitmapOptions)
    }
}

/**
 * Size of a bitmap
 */
suspend fun Uri.loadBitmapSize(contentResolver: ContentResolver) = async(CommonPool) {
    val onlyBoundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inPreferredConfig = Bitmap.Config.ARGB_8888//optional
    }
    contentResolver.openInputStream(this@loadBitmapSize).use { input ->
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
    }

    return@async if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
        null
    } else {
        onlyBoundsOptions
    }
}

/**
 *
 */
suspend fun Bitmap.compress(@IntRange(from = 0L, to = 100L) compressionPercentage: Int): Deferred<Bitmap> = async(CommonPool) {
    ByteArrayOutputStream().use { out ->
        this@compress.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, out)
        return@async BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
    }
}

suspend fun Uri.loadBitmapPreviews(scalePreviewsPercentages: IntArray,
                                   width: Int,
                                   contentResolver: ContentResolver): Deferred<List<Bitmap>?>
        = async(CommonPool) {
    val bitmap = this@loadBitmapPreviews.loadBitmapScaled(contentResolver, width).await() ?: return@async null
    val compressSizes = scalePreviewsPercentages.map {
        bitmap.compress(it)
    }
    return@async compressSizes.map { it.await() }

}

@IntRange(from = 1)
private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
    val k = Integer.highestOneBit(Math.floor(ratio).toInt())
    return maxOf(k, 1)
}


fun Context.calculateOptimalThumbnailSize() {

}