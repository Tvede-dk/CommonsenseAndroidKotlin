package com.commonsense.android.kotlin.system.imaging

import android.graphics.Bitmap
import android.widget.ImageView
import com.commonsense.android.kotlin.base.concurrency.LimitedCoroutineCounter
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.logging.L
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

/**
 * Created by Kasper Tvede on 27-08-2017.
 */
typealias ImageLoaderType = suspend () -> Bitmap?

typealias ImageDecodingType = suspend (Bitmap) -> Bitmap?

class ImageLoader(loadingCapacity: Int = 3, decodingScalingCapacity: Int = 2) {

    companion object {
        val instance = ImageLoader()
    }

    private val loadingSemaphore = LimitedCoroutineCounter(loadingCapacity)
    private val decodingSemaphore = LimitedCoroutineCounter(decodingScalingCapacity)


    private val idsToCancel = mutableSetOf<String>()

    private val mutexForIds = Mutex(false)


    suspend fun unschedualeId(id: String) = mutexForIds.withLock {
        idsToCancel.add(id)
    }

    suspend fun loadAndScale(id: String, loader: ImageLoaderType, decoder: ImageDecodingType): Bitmap? {
        removeIdFromCancel(id) //remove the id from cancel if we are to load it now.
        return loadingSemaphore.perform {
            L.error("test", "\tload stage")
            loadStage(id, loader, decoder)
        }
    }

    private suspend fun loadStage(id: String, loader: ImageLoaderType, decoder: ImageDecodingType): Bitmap? {

        if (shouldCancel(id)) {
            return null
        }

        return loader()?.let { bitmap ->
            decodingSemaphore.perform {
                L.error("test", "\t\tdecode stage")
                decodeStage(id, bitmap, decoder)
            }
        }
    }


    private suspend fun decodeStage(id: String, bitmap: Bitmap, decoder: ImageDecodingType): Bitmap? {
        if (shouldCancel(id)) {
            return null
        }
        return decoder(bitmap)
    }

    private suspend fun removeIdFromCancel(id: String) = mutexForIds.withLock {
        idsToCancel.remove(id)
    }

    private suspend fun shouldCancel(id: String): Boolean = mutexForIds.withLock {
        idsToCancel.contains(id).ifTrue { idsToCancel.remove(id) }
    }
//
//    /**
//     * Terminates all loading and decoding. all calls hereafter will fail and or possibly throw.
//     */
//    fun close() {
//        if (instance === this) {
//            L.error("ImageLoader", " Not allowed to terminate the singleton instance.")
//            return
//        }
////        loadingChannel.close()
////        decodingChannel.close()
//    }
}


suspend fun ImageView.loadImage(id: String, loader: ImageLoaderType, decoder: ImageDecodingType, imageLoader: ImageLoader = ImageLoader.instance) {
    imageLoader.loadAndScale(id, loader, decoder)?.let {
        launch(UI) { this@loadImage.setImageBitmap(it) }
    }
}

