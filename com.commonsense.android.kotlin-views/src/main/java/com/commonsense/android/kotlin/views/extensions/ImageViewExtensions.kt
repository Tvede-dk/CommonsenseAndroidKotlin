package com.commonsense.android.kotlin.views.extensions

import android.support.annotation.ColorInt
import android.widget.ImageView
import com.commonsense.android.kotlin.system.imaging.withColor
import com.commonsense.android.kotlin.views.features.clearTag
import com.commonsense.android.kotlin.views.features.getTag
import com.commonsense.android.kotlin.views.features.setTag
import com.commonsense.android.kotlin.views.features.useTagOr
import kotlinx.coroutines.experimental.Job
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
 * also cancels the "older" operation.
 */
fun <T> ImageView.loadAndUse(action: suspend () -> T?, actionAfter: (T, ImageView) -> Unit) {
    val name = ImageView::class.java.name + "." + "loadAndUse"
    val nameJob = name + "job"
    useTagOr(name, {
        val ourStartIndex = it.incrementAndGet()
        getTag<Job>(nameJob)?.cancel()
        setTag(nameJob, launch(UI) {
            val result = action()
            if (getTag<AtomicInteger>(name)?.get() == ourStartIndex
                    && result != null) {
                actionAfter(result, this@loadAndUse)
                clearTag(nameJob)
            }
        })
    }, initialValue = {
        AtomicInteger(0)
    })

}
