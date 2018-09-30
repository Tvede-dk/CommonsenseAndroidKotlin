@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.graphics.*
import android.support.annotation.*
import android.widget.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.imaging.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.features.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*
import java.util.concurrent.atomic.*

/**
 * Created by Kasper Tvede on 24-07-2017.
 */

/**
 * Creates a color overlay for this image view
 * @receiver ImageView
 * @param color Int the color to apply to the drawable.
 */
fun ImageView.colorOverlay(@ColorInt color: Int) {
    drawable?.withColor(color)?.let(this::setImageDrawable)
}

/**
 * This is safe for use on lists, ect, where calling it multiple times will yield only the last as the real modifier.
 * does not call "cancel" on the older action.
 */
fun ImageView.loadAndUse(loading: ImageLoaderType,
                         decodeScale: ImageDecodingType,
                         afterDecoded: Function2<ImageView, Bitmap, Unit>) {
    val index = counterTag
    val ourIndex = index.incrementAndGet()

    GlobalScope.launch(Dispatchers.Main) {
        //make sure the UI is visible TODO..
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

/**
 *
 * @param ourIndex Int
 * @param index AtomicInteger
 * @param action AsyncEmptyFunctionResult<T?>
 * @return AsyncEmptyFunctionResult<T?>
 */
private fun <T> validateId(ourIndex: Int,
                           index: AtomicInteger,
                           action: AsyncEmptyFunctionResult<T?>): AsyncEmptyFunctionResult<T?> =
        (ourIndex == index.get()).map(action, { null })

/**
 *
 * @param ourIndex Int
 * @param index AtomicInteger
 * @param action AsyncFunction1<U, T>
 * @return (U) -> T?
 */
private fun <T, U> validateIdWith(ourIndex: Int,
                                  index: AtomicInteger,
                                  action: AsyncFunction1<U, T>): suspend (U) -> T? {
    return (ourIndex == index.get()).map(action, { null })
}

/**
 * For handling the tag on the imageView
 */
private const val imageViewCounterTag = "ImageView.counterTag"
/**
 *
 */
private val ImageView.counterTag
    get() = getTagOr(imageViewCounterTag) { AtomicInteger(0) }
