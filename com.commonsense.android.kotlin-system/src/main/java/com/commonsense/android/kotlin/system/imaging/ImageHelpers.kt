package com.commonsense.android.kotlin.system.imaging

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.media.ExifInterface
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.system.extensions.getVirtualScreenSize
import com.commonsense.android.kotlin.system.logging.tryAndLogSuspend
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream


/**
 * Created by Kasper Tvede on 11-07-2017.
 */
data class ImageSize(val width: Int, val height: Int) {
    override fun toString(): String = "$width-$height"
}

suspend fun Uri.loadBitmapWithSampleSize(contentResolver: ContentResolver, ratio: Double, containsTransparency: Boolean = true): Deferred<Bitmap?> = async(CommonPool) {
    val bitmapConfig = containsTransparency.map(Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565)
    val bitmapOptions = BitmapFactory.Options().apply {
        @IntRange(from = 1)
        inSampleSize = getPowerOfTwoForSampleRatio(ratio)
        inPreferredConfig = bitmapConfig//
        /*     inDensity = srcWidth //TODO do this later, as this will result in perfecter scaling.
             inScaled = true
             inTargetDensity = dstWidt * inSampleSize*/
    }

    contentResolver.openInputStream(this@loadBitmapWithSampleSize).use { inputToDecode ->
        return@async BitmapFactory.decodeStream(inputToDecode, null, bitmapOptions)
    }
}

/**
 * Size of a bitmap
 */
suspend fun Uri.loadBitmapSize(contentResolver: ContentResolver, bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888): Deferred<BitmapFactory.Options?> = async(CommonPool) {
    val onlyBoundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inPreferredConfig = bitmapConfig//optional
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

suspend fun Bitmap.scaleToWidth(@IntRange(from = 0) width: Int): Deferred<Bitmap?> = async(CommonPool) {
    tryAndLogSuspend("Bitmap.scaleToWidth") {
        val size = getImageSize().scaleWidth(width)
        Bitmap.createScaledBitmap(this@scaleToWidth, size.width, size.height, true)
    }
}

/**
 * Assuming a thumbnail is square
 *
 */
fun Context.calculateOptimalThumbnailSize(defaultSize: Int = 200, minSize: Int = 50, fraction: Int = 8): Int {
    val virtualSize = getVirtualScreenSize() ?: return defaultSize
    val widthFraction = virtualSize.x / fraction
    val heightFraction = virtualSize.y / fraction
    val combined = widthFraction + heightFraction
    return maxOf(combined / 2, minSize)
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


suspend fun Bitmap.rotate(@FloatRange(from = 0.0, to = 360.0) degrees: Float): Deferred<Bitmap> = async(CommonPool) {
    val matrix = Matrix()
    if (degrees != 0f) {
        matrix.preRotate(degrees)
    }
    return@async Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
}

suspend fun Uri.loadBitmapRotatedCorrectly(contentResolver: ContentResolver, width: Int): Deferred<Bitmap?> = async(CommonPool) {
    tryAndLogSuspend("loadImage") {
        val exif = getExifForImage(contentResolver).await()
        val bitmap = loadBitmapScaled(contentResolver, width).await()
        return@tryAndLogSuspend bitmap?.rotate(exif)?.await()
    }
}

suspend fun Uri.getExifForImage(contentResolver: ContentResolver) = async(CommonPool) {
    contentResolver.openInputStream(this@getExifForImage).use { input ->
        return@async ExifInterface(input)
    }
}


fun Bitmap.outputTo(outputStream: OutputStream, @IntRange(from = 0, to = 100) quality: Int, format: Bitmap.CompressFormat) {
    compress(format, quality, outputStream)
}

/**
 * Allows to save a bitmap to the given location, using the supplied arguements for controlling the quality / format.
 *
 */
fun Bitmap.saveTo(path: Uri, contentResolver: ContentResolver, @IntRange(from = 0, to = 100) quality: Int,
                  format: Bitmap.CompressFormat) = async(CommonPool) {
    contentResolver.openOutputStream(path).use {
        this@saveTo.outputTo(it, quality, format)
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
    return@async loadBitmapWithSampleSize(contentResolver, ratio).await()
}


val ImageSize.largest: Int
    get() = maxOf(width, height)


fun ImageSize.scaleWidth(destWidth: Int): ImageSize {
    val destFloat = destWidth.toFloat()
    val srcFloat = width.toFloat()
    val ratio = (1f / (maxOf(destFloat, srcFloat) / minOf(destFloat, srcFloat)))
    return applyRatio(ratio)
}

fun ImageSize.applyRatio(ratio: Float): ImageSize =
        ImageSize((width * ratio).toInt(), (height * ratio).toInt())


fun Bitmap.getImageSize(): ImageSize = ImageSize(width, height)