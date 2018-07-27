package com.commonsense.android.kotlin.system.base.helpers

import android.content.Context
import android.support.annotation.LayoutRes
import com.commonsense.android.kotlin.system.resourceHandling.BaseAsyncLayoutInflater
import kotlinx.coroutines.experimental.awaitAll


suspend fun Context.preloadViews(viewsToPreload: LayoutResList) {
    if (viewsToPreload.views.isEmpty()) {
        return
    }
    BaseAsyncLayoutInflater(this@preloadViews).apply {
        viewsToPreload.views.map { it ->
            inflateNoFallback(it, null)
        }.awaitAll()
    }
}


class LayoutResList internal constructor(@LayoutRes vararg ids: Int) {
    val views: IntArray = ids
}

fun resListOf(@LayoutRes vararg ids: Int): LayoutResList = LayoutResList(*ids)
