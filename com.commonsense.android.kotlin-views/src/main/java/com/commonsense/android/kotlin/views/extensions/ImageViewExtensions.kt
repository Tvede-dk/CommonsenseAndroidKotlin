package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.ColorInt
import android.widget.ImageView
import com.commonsense.android.kotlin.system.imaging.withColor
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
fun <T> ImageView.loadAndUse(action: suspend () -> T?, actionAfter: (T, ImageView) -> Unit) {
    val index = counterTag
    val ourIndex = index.incrementAndGet()
    launch(UI) {
        val result = action()
        if (index.get() == ourIndex
                && result != null) {
            actionAfter(result, this@loadAndUse)
        }
    }

}

private val imageViewCounterTag = "ImageView" + "." + "counterTag"
private val ImageView.counterTag
    get() = getTagOr(imageViewCounterTag, { AtomicInteger(0) })
