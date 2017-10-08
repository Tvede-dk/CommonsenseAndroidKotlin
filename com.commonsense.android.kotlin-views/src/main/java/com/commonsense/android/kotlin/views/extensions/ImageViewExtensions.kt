package com.commonsense.android.kotlin.views.extensions

import android.graphics.Bitmap
import android.support.annotation.ColorInt
import android.widget.ImageView
import com.commonsense.android.kotlin.system.imaging.ImageDecodingType
import com.commonsense.android.kotlin.system.imaging.ImageLoader
import com.commonsense.android.kotlin.system.imaging.ImageLoaderType
import com.commonsense.android.kotlin.system.imaging.withColor
import com.commonsense.android.kotlin.system.logging.tryAndLogSuspend
import com.commonsense.android.kotlin.views.features.getTagOr
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Kasper Tvede on 24-07-2017.
 */

fun ImageView.colorOverlay(@ColorInt color: Int) {
    drawable?.withColor(color)?.let(this::setImageDrawable)
}

/**
 * This is safe for use on lists, ect, where calling it multiple times will yield only the last as the real modifier.
 * does not call "cancel" on the older action.
 */
fun ImageView.loadAndUse(loading: ImageLoaderType, decodeScale: ImageDecodingType, afterDecoded: (ImageView, Bitmap) -> Unit) {
    val index = counterTag
    val ourIndex = index.incrementAndGet()
    launch(UI) {

        tryAndLogSuspend("ImageView.loadAndUse") {
            val bitmap = ImageLoader.instance.loadAndScale(
                    ourIndex.toString(),
                    validateId(ourIndex, index, loading),
                    validateIdWith(ourIndex, index, decodeScale))
            if (index.get() == ourIndex && bitmap != null) {
                afterDecoded(this@loadAndUse, bitmap)
            }
        }
    }
}

private fun <T> validateId(ourIndex: Int, index: AtomicInteger, action: suspend () -> T?): suspend () -> T? {
    return {
        if (ourIndex == index.get()) {
            action()
        } else {
            null
        }
    }
}

private fun <T, U> validateIdWith(ourIndex: Int, index: AtomicInteger, action: suspend (U) -> T?): suspend (U) -> T? {
    return {
        if (ourIndex == index.get()) {
            action(it)
        } else {
            null
        }
    }
}

private val imageViewCounterTag = "ImageView.counterTag"
private val ImageView.counterTag
    get() = getTagOr(imageViewCounterTag, { AtomicInteger(0) })
