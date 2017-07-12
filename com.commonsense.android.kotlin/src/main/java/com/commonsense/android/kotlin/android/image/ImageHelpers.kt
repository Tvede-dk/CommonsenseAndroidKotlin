package com.commonsense.android.kotlin.android.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.support.annotation.IntRange
import android.support.media.ExifInterface
import com.commonsense.android.kotlin.android.extensions.GetVirtualScreenSize
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import map
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


/**
 * Created by Kasper Tvede on 11-07-2017.
 */


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
    val bitmap = this@loadBitmapPreviews.loadBitmapRotatedCorrectly(contentResolver, width).await() ?: return@async null
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

/**
 * Assuming a thumbnail is square
 *
 */
fun Context.calculateOptimalThumbnailSize(defaultSize: Int = 200, minSize: Int = 50, fraction: Int = 8): Int {
    val virtualSize = GetVirtualScreenSize() ?: return defaultSize
    val widthFraction = virtualSize.x / fraction
    val heightFraction = virtualSize.y / fraction
    val combined = widthFraction + heightFraction
    val resultSize = maxOf(combined / 2, minSize)
    return resultSize
}


suspend fun Bitmap.rotate(exifInterface: ExifInterface): Deferred<Bitmap> = async(CommonPool) {
    var rotation = 0F
    val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90F
        ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180F
        ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270F
    }
    return@async this@rotate.rotate(rotation).await()
}


suspend fun Bitmap.rotate(@IntRange(from = 0, to = 360) degrees: Float): Deferred<Bitmap> = async(CommonPool) {
    val matrix = Matrix()
    if (degrees != 0f) {
        matrix.preRotate(degrees)
    }
    return@async Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
}

suspend fun Uri.loadBitmapRotatedCorrectly(contentResolver: ContentResolver, width: Int): Deferred<Bitmap?> = async(CommonPool) {
    val exif = getExifForImage(contentResolver).await()
    val bitmap = loadBitmapScaled(contentResolver, width).await()
    val rotatedBitmap = bitmap?.rotate(exif)?.await()
    return@async rotatedBitmap
}

suspend fun Uri.getExifForImage(contentResolver: ContentResolver) = async(CommonPool) {
    contentResolver.openInputStream(this@getExifForImage).use { input ->
        val exif = ExifInterface(input)
        return@async exif
    }
}

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