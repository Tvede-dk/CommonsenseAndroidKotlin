@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.commonsense.android.kotlin.system.imaging

import android.content.*
import android.graphics.*
import android.net.*
import androidx.annotation.*
import androidx.exifinterface.media.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.*
import java.io.*


/**
 *
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @param ratio Double
 * @param containsTransparency Boolean
 * @return Deferred<Bitmap?>
 */
fun Uri.loadBitmapWithSampleSize(contentResolver: ContentResolver,
                                 ratio: Double,
                                 containsTransparency: Boolean = true,
                                 shouldDownSample: Boolean = false): Deferred<Bitmap?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    val bitmapConfig = containsTransparency.map(Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565)
    val bitmapOptions = BitmapFactory.Options().apply {
        @androidx.annotation.IntRange(from = 1)
        inSampleSize = getPowerOfTwoForSampleRatio(ratio, shouldDownSample)
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
 *
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @param bitmapConfig Bitmap.Config
 * @return Deferred<BitmapFactory.Options?>
 */
fun Uri.loadBitmapSize(contentResolver: ContentResolver, bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888): Deferred<BitmapFactory.Options?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
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
 * @receiver Bitmap
 * @param compressionPercentage Int
 * @return Deferred<Bitmap>
 */
fun Bitmap.compress(@androidx.annotation.IntRange(from = 0L, to = 100L) compressionPercentage: Int): Deferred<Bitmap> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    ByteArrayOutputStream().use { out ->
        this@compress.compress(Bitmap.CompressFormat.JPEG, compressionPercentage, out)
        return@async BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
    }
}

suspend fun Uri.loadBitmapPreviews(scalePreviewsPercentages: IntArray,
                                   width: Int,
                                   contentResolver: ContentResolver): Deferred<List<Bitmap>?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    val bitmap = this@loadBitmapPreviews.loadBitmapRotatedCorrectly(contentResolver, width).await()
            ?: return@async null
    val compressSizes = scalePreviewsPercentages.map {
        bitmap.compress(it)
    }
    return@async compressSizes.map { it.await() }
}

/**
 *
 * @param ratio Double
 * @return Int
 */
@androidx.annotation.IntRange(from = 1)
fun getPowerOfTwoForSampleRatio(ratio: Double, downSample: Boolean): Int {
    val scaledRatio = if (downSample) {
        Math.floor(ratio)
    } else {
        Math.ceil(ratio)
    }
    val k = Integer.highestOneBit(scaledRatio.toInt())
    return maxOf(k, 1)
}

/**
 *
 * @receiver Bitmap
 * @param width Int
 * @return Deferred<Bitmap?>
 */
suspend fun Bitmap.scaleToWidth(@androidx.annotation.IntRange(from = 0) width: Int): Deferred<Bitmap?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    tryAndLogSuspend("Bitmap.scaleToWidth") {
        val size = getImageSize().scaleWidth(width)
        Bitmap.createScaledBitmap(this@scaleToWidth, size.width, size.height, true)
    }
}

/**
 *
 * Assumes a thumbnail is square
 * @receiver Context
 * @param defaultSize Int
 * @param minSize Int
 * @param fraction Int
 * @return Int
 */
@androidx.annotation.IntRange(from = 0)
fun Context.calculateOptimalThumbnailSize(@androidx.annotation.IntRange(from = 0) defaultSize: Int = 200,
                                          @androidx.annotation.IntRange(from = 0) minSize: Int = 50,
                                          @androidx.annotation.IntRange(from = 0) fraction: Int = 8): Int {
    val virtualSize = getVirtualScreenSize() ?: return defaultSize
    val widthFraction = virtualSize.x / fraction
    val heightFraction = virtualSize.y / fraction
    val combined = widthFraction + heightFraction
    return maxOf(combined / 2, minSize)
}

/**
 *
 * @receiver Bitmap
 * @param exifInterface ExifInterface
 * @return Deferred<Bitmap>
 */
suspend fun Bitmap.rotate(exifInterface: ExifInterface): Deferred<Bitmap> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    var rotation = 0F
    val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180F
        ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90F
        ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270F
    }
    return@async this@rotate.rotate(rotation).await()
}


/**
 *
 * @receiver Bitmap
 * @param degrees Float
 * @return Deferred<Bitmap>
 */
fun Bitmap.rotate(@FloatRange(from = 0.0, to = 360.0) degrees: Float): Deferred<Bitmap> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    val matrix = Matrix()
    if (degrees != 0f) {
        matrix.preRotate(degrees)
    }
    return@async Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
}

/**
 *
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @param width Int
 * @return Deferred<Bitmap?>
 */
suspend fun Uri.loadBitmapRotatedCorrectly(contentResolver: ContentResolver, width: Int): Deferred<Bitmap?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    tryAndLogSuspend("loadImage") {
        val exif = getExifForImage(contentResolver).await()
        val bitmap = loadBitmapScaled(contentResolver, width).await()
        if (exif != null) {
            bitmap?.rotate(exif)?.await()
        } else {
            bitmap
        }
    }
}

/**
 *
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @return Deferred<ExifInterface?>
 */
fun Uri.getExifForImage(contentResolver: ContentResolver) = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    contentResolver.openInputStream(this@getExifForImage)?.use { input ->
        return@async ExifInterface(input)
    }
}

/**
 *
 * @receiver Bitmap
 * @param outputStream OutputStream
 * @param quality Int
 * @param format Bitmap.CompressFormat
 */
fun Bitmap.outputTo(outputStream: OutputStream, @androidx.annotation.IntRange(from = 0, to = 100) quality: Int, format: Bitmap.CompressFormat) {
    compress(format, quality, outputStream)
}

/**
 * Allows to save a bitmap to the given location, using the supplied arguements for controlling the quality / format.
 * @receiver Bitmap
 * @param path Uri
 * @param contentResolver ContentResolver
 * @param quality Int
 * @param format Bitmap.CompressFormat
 * @return Deferred<Unit?>
 */
fun Bitmap.saveTo(path: Uri, contentResolver: ContentResolver, @androidx.annotation.IntRange(from = 0, to = 100) quality: Int,
                  format: Bitmap.CompressFormat) = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    contentResolver.openOutputStream(path)?.use {
        this@saveTo.outputTo(it, quality, format)
    }
}


/**
 * want to be able to load a "scaled bitmap"
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @param width Int
 * @return Deferred<Bitmap?>
 */
suspend fun Uri.loadBitmapScaled(contentResolver: ContentResolver, width: Int): Deferred<Bitmap?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) {
    val onlyBoundsOptions = this@loadBitmapScaled.loadBitmapSize(contentResolver).await()
            ?: return@async null
    val originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
            .map(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth)
    val ratio = (originalSize > width).map(originalSize / width.toDouble(), 1.0)
    return@async loadBitmapWithSampleSize(contentResolver, ratio).await()
}

/**
 *
 * @receiver Bitmap
 * @return ImageSize
 */
fun Bitmap.getImageSize(): ImageSize = ImageSize(width, height)


/**
 *
 * @property width Int
 * @property height Int
 * @constructor
 */
data class ImageSize(val width: Int, val height: Int) {
    override fun toString(): String = "$width-$height"
}

/**
 *
 */
val ImageSize.largest: Int
    get() = maxOf(width, height)


/**
 * Scales the imaage to the max width given.
 * @receiver ImageSize
 * @param destWidth Int
 * @return ImageSize
 */
fun ImageSize.scaleWidth(destWidth: Int): ImageSize {
    val destFloat = destWidth.toFloat()
    val srcFloat = width.toFloat()
    val ratio = (1f / (maxOf(destFloat, srcFloat) / minOf(destFloat, srcFloat)))
    return applyRatio(ratio)
}

/**
 *
 * @receiver ImageSize
 * @param ratio Float
 * @return ImageSize
 */
fun ImageSize.applyRatio(ratio: Float): ImageSize =
        ImageSize((width * ratio).toInt(), (height * ratio).toInt())
