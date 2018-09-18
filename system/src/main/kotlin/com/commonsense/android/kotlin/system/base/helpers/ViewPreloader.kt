package com.commonsense.android.kotlin.system.base.helpers

import android.content.*
import android.support.annotation.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.system.resourceHandling.*


/**
 * Simply preloads all the given views via the BaseAsyncLayoutInflater
 * @receiver Context
 * @param viewsToPreload LayoutResList the views to preload
 */
suspend fun Context.preloadViews(viewsToPreload: LayoutResList) {
    if (viewsToPreload.isNotEmpty()) {
        viewsToPreload.inflateAllNoFallbackAndAwait(
                BaseAsyncLayoutInflater(this@preloadViews))
    }
}

/**
 * A Wrapper over a very strongly typed list of @LayoutRes id's
 */
class LayoutResList internal constructor(@LayoutRes vararg ids: Int) {
    /**
     * All the containted views of @LayoutRes type
     */
    val views: IntArray = ids

    /**
     * Inflates all the views contained, using the NoFallback (background) mode.
     * @param inflater BaseAsyncLayoutInflater
     */
    suspend fun inflateAllNoFallbackAndAwait(inflater: BaseAsyncLayoutInflater) {
        views.map { inflater.inflateNoFallback(it, null) }.awaitAll()
    }

    /**
     * Tells if this list is empty
     * @return Boolean true if this list is empty
     */
    fun isEmpty() = views.isEmpty()

    /**
     * Tells if this list is not empty
     * @return Boolean true if this list is not empty; inverse of isEmpty.
     */
    fun isNotEmpty() = views.isNotEmpty()
}

/**
 * Creates a LayoutResList of the given layout ids.
 * @param ids IntArray
 * @return LayoutResList
 */
fun resListOf(@LayoutRes vararg ids: Int): LayoutResList = LayoutResList(*ids)
