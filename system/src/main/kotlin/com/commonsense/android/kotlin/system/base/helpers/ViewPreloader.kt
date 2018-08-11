package com.commonsense.android.kotlin.system.base.helpers

import android.content.*
import android.support.annotation.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.system.resourceHandling.*


suspend fun Context.preloadViews(viewsToPreload: LayoutResList) {
    if (viewsToPreload.isNotEmpty()) {
        viewsToPreload.inflateAllNoFallbackAndAwait(
                BaseAsyncLayoutInflater(this@preloadViews))
    }
}


class LayoutResList internal constructor(@LayoutRes vararg ids: Int) {
    /**
     * All the containted views of @LayoutRes type
     */
    val views: IntArray = ids

    /**
     * Inflates all the views contained, using the NoFallback (background) mode.
     */
    suspend fun inflateAllNoFallbackAndAwait(inflater: BaseAsyncLayoutInflater) {
        views.map { inflater.inflateNoFallback(it, null) }.awaitAll()
    }

    fun isEmpty() = views.isEmpty()
    fun isNotEmpty() = views.isNotEmpty()
}

fun resListOf(@LayoutRes vararg ids: Int): LayoutResList = LayoutResList(*ids)
