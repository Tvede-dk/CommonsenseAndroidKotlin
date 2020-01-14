@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.resourceHandling

import android.content.*
import android.support.annotation.*
import android.util.*
import android.view.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import kotlinx.coroutines.sync.*
import java.util.concurrent.*


/**
 *
 * Inspiration
 * https://android.googlesource.com/platform/frameworks/support/+/master/core-ui/src/main/java/android/support/v4/view/AsyncLayoutInflater.java
 * or just simply
 * AsyncLayoutInflater
 * its however not limited to one thread only, unless specified
 */
class BaseAsyncLayoutInflater(val context: Context,
                              maxThreads: Int = 4) {

    private val tag = BaseAsyncLayoutInflater::class.java.simpleName

    private val inflaterQueue: ExecutorCoroutineDispatcher =
            Executors.newFixedThreadPool(maxThreads).asCoroutineDispatcher()

    private val inflaters = maxThreads.mapEach {
        LockableInflater(context)
    }


    @Throws(Throwable::class)
    @UiThread
    suspend fun inflate(@LayoutRes id: Int,
                        parent: ViewGroup? = null)
            : Deferred<View> = GlobalScope.async(inflaterQueue, block = {
        backgroundInflateView(id, parent)
                ?: inflateViewInUIThread(id, parent)
    })

    @UiThread
    suspend fun inflateNoFallback(@LayoutRes id: Int,
                                  parent: ViewGroup? = null)
            : Deferred<View?> = GlobalScope.async(inflaterQueue, block = {
        backgroundInflateView(id, parent)
    })


    @UiThread
    fun inflateLayoutFrom(infFunc: Function1<LayoutInflater, View?>)
            : Deferred<View?> = GlobalScope.async(inflaterQueue) {
        tryAndLogSuspend(tag) {
            inflateViewViaInflaters {
                infFunc(it.inflater)
            }
        }
    }

    private suspend fun inflateViewInUIThread(id: Int, parent: ViewGroup?): View {
        val view = withContext(Dispatchers.Main) {
            inflateViewViaInflaters(id, parent)
        }
        L.warning(tag, "Inflating via ui Thread, for layout id : $id")
        return view ?: throw RuntimeException("could not inflate view via main thread\n" +
                "\t - layout id: $id" +
                "\t - parent : $parent")
    }


    @WorkerThread
    private suspend fun backgroundInflateView(id: Int, parent: ViewGroup?): View? =
            tryAndLogSuspend(tag,
                    "Failed to inflate resource in the background\n" +
                            "\t - layout id : $id\t" +
                            "\t -  parent: $parent", L::warning) {
                inflateViewViaInflaters(id, parent)
            }

    fun destroy() {
        //stop all
        inflaterQueue.cancelChildren()
        //close it.
        inflaterQueue.close()
    }

    private suspend inline fun inflateViewViaInflaters(id: Int, parent: ViewGroup?): View? =
            inflateViewViaInflaters {
                it.inflater.inflate(id, parent, false)
            }

    private suspend inline fun inflateViewViaInflaters(crossinline callback: Function1<LockableInflater, View?>): View? {
        return inflaters.retrieveInflaterOrCreate(context).use {
            callback(this)
        }
    }
}

internal suspend fun <U> LockableInflater.use(action: LockableInflater.() -> U): U =
        mutex.withLock { action() }


internal class BaseLayoutInflater(context: Context) : LayoutInflater(
        context.layoutInflater?.cloneInContext(context) ?: from(context), context) {

    private val cache by lazy {
        LayoutInflaterCache()
    }

    override fun cloneInContext(context: Context?): LayoutInflater {
        val safeContext = context ?: throw IllegalArgumentException("given context is null.")
        return BaseLayoutInflater(safeContext)
    }

    override fun onCreateView(name: String?, attrs: AttributeSet?): View {
        try {
            return cache.createView(name ?: "", attrs, this) ?: super.onCreateView(name, attrs)
        } catch (e: ClassNotFoundException) {
            throw e
        }
    }

}

internal class LayoutInflaterCache {

    /**
     *
     */
    private val foundMap: MutableMap<String, String?> = mutableMapOf(
            "Button" to widgetPackage,
            "LinearLayout" to widgetPackage,
            "RelativeLayout" to widgetPackage,
            "EditText" to widgetPackage,
            "GridView" to widgetPackage,
            "GridLayout" to widgetPackage,
            "ImageView" to widgetPackage,
            "ListView" to widgetPackage,
            "ScrollView" to widgetPackage,
            "TextView" to widgetPackage,
            "StackView" to widgetPackage,
            "Switch" to widgetPackage,
            "Toolbar" to widgetPackage,
            "Checkbox" to widgetPackage,
            "FrameLayout" to widgetPackage,
            "SurfaceView" to viewPackage,
            "TextureView" to viewPackage,
            "WebView" to webkitPackage)

    fun createView(name: String, attrs: AttributeSet?, inflater: LayoutInflater): View? = tryAndLog("createView") {
        val optValue = foundMap[name]
        if (optValue != null) {
            return@tryAndLog inflater.createView(name, optValue, attrs)
        }
        predefinedPrefixes.forEach { prefix ->
            try {
                val view = inflater.createView(name, prefix, attrs)
                if (view != null) {
                    foundMap[name] = prefix
                    return@tryAndLog  view
                }
            } catch (e: ClassNotFoundException) {
                //"wrong" guess.. try again or return null.
            }
        }
        return@tryAndLog  null
    }

    /**
     *
     */
    companion object {
        const val widgetPackage = "android.widget."
        const val webkitPackage = "android.webkit."
        const val viewPackage = "android.view."
        val predefinedPrefixes = listOf(
                widgetPackage,
                viewPackage,
                webkitPackage,
                "android.app.")
    }

}

/**
 *
 * @property mutex Mutex
 * @property inflater BaseLayoutInflater
 * @constructor
 */
internal class LockableInflater(context: Context) {
    val mutex = Mutex(false)
    val inflater by lazy { BaseLayoutInflater(context) }
}


internal fun List<LockableInflater>.retrieveInflaterOrCreate(context: Context): LockableInflater {
    return retrieveInflater() ?: LockableInflater(context)
}

internal fun List<LockableInflater>.retrieveInflater(): LockableInflater? {
    return find { !it.mutex.isLocked }
}