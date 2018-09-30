@file:Suppress("unused")

package com.commonsense.android.kotlin.system.base.helpers

import android.app.*
import android.content.*
import android.os.*
import android.support.annotation.IntRange
import android.support.v4.app.*
import android.util.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.debug.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*


/**
 * Created by Kasper Tvede
 */

/**
 *
 */
typealias ActivityResultCallback = (resultCode: Int, data: Intent?) -> Unit

/**
 *
 */
typealias ActivityResultCallbackOk = (data: Intent?) -> Unit

/**
 *
 */
typealias AsyncActivityResultCallback = suspend (resultCode: Int, data: Intent?) -> Unit

/**
 *
 */
typealias AsyncActivityResultCallbackOk = suspend (data: Intent?) -> Unit


/**
 *
 */
class ActivityResultHelper(warningLogger: FunctionUnit<String>? = null) {

    private val weakLogger = warningLogger.weakReference()

    private val activityResultListeners by lazy {
        SparseArray<ActivityResultCallbackInterface>()
    }

    /**
     * Removes all listeners.
     */
    fun clear() {
        activityResultListeners.clear()
    }

    /**
     * calls the appropriated listener, iff there is one; if not logs the incident to the logger.
     */
    fun handle(requestCode: Int, resultCode: Int, data: Intent?) {
        val listener = activityResultListeners[requestCode]
        listener?.onActivityResult(resultCode, data)
        listener.ifNull {
            weakLogger.use("Actual listener not found for request code $requestCode")
        }
    }

    /**
     * removes a listener based on request code.
     */
    fun remove(@IntRange(from = 0) requestCode: Int) {
        activityResultListeners.remove(requestCode)
    }

    fun addForAllResults(@IntRange(from = 0) requestCode: Int, receiver: ActivityResultCallback) {
        addReceiver(ActivityResultWrapper({ resultCode, data ->
            receiver(resultCode, data)
        }, requestCode, this::remove))
    }

    fun addForOnlyOk(@IntRange(from = 0) requestCode: Int, receiver: ActivityResultCallbackOk) {
        addForAllResults(requestCode) { resultCode: Int, data: Intent? ->
            resultCode.isOkResult().ifTrue { receiver(data) }
        }
    }

    fun addForAllResultsAsync(@IntRange(from = 0) requestCode: Int, receiver: AsyncActivityResultCallback) {
        addReceiver(AsyncActivityResultWrapper({ resultCode, data ->
            receiver(resultCode, data)
        }, requestCode, this::remove))
    }

    fun addForOnlyOkAsync(@IntRange(from = 0) requestCode: Int, receiver: AsyncActivityResultCallbackOk) {
        addForAllResultsAsync(requestCode, { resultCode: Int, data: Intent? ->
            if (resultCode.isOkResult()) {
                receiver(data)
            }
        })
    }

    private fun addReceiver(receiver: ActivityResultCallbackInterface) {
        if (activityResultListeners[receiver.requestCode] != null) {
            weakLogger.get()?.invoke("Overwriting an actual listener, for request code ${receiver.requestCode}")
            throw RuntimeException("Overwriting an actual listener, this is unsupported / not allowed behavior.")
        }
        activityResultListeners.put(receiver.requestCode, receiver)
    }

    fun toPrettyString(): String {
        return "Activity Result helper state:\n\t" +
                activityResultListeners.map { "${it.requestCode} => $it" }
                        .prettyStringContent(
                                "\tActivity result listeners:",
                                "\tThere are no listeners.")
    }

    override fun toString() = toPrettyString()

}

private fun Int.isOkResult(): Boolean = this == Activity.RESULT_OK


//<editor-fold desc="Interfaces">
interface ActivityResultCallbackInterface {
    fun onActivityResult(resultCode: Int, data: Intent?)
    val requestCode: Int
}


interface ActivityResultHelperContainer {
    /**
     *
     */
    fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk)

    /**
     *
     */
    fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback)

    /**
     *
     */
    fun addActivityResultListenerOnlyOkAsync(requestCode: Int, receiver: AsyncActivityResultCallbackOk)

    /**
     *
     */
    fun addActivityResultListenerAsync(requestCode: Int, receiver: AsyncActivityResultCallback)

    /**
     *
     */
    fun removeActivityResultListener(@IntRange(from = 0) requestCode: Int)
}
//</editor-fold>


//<editor-fold desc="Wrappers">
class ActivityResultWrapper(val callback: ActivityResultCallback,
                            override val requestCode: Int,
                            val removerFunction: (requestCode: Int) -> Unit)
    : ActivityResultCallbackInterface {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        callback(resultCode, data)
        removerFunction(requestCode)
    }
}

class AsyncActivityResultWrapper(val callback: AsyncActivityResultCallback,
                                 override val requestCode: Int,
                                 val removerFunction: (requestCode: Int) -> Unit)
    : ActivityResultCallbackInterface {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, {
            callback(resultCode, data)
            removerFunction(requestCode)
        })
    }
}
//</editor-fold>


//<editor-fold desc="Base activity hook points">
fun BaseActivity?.startActivityForResult(intent: Intent,
                                         options: Bundle?,
                                         requestCode: Int,
                                         activityResultCallback: ActivityResultCallbackOk) {
    if (this == null) {
        logBadActivity()
        return
    }
    addActivityResultListenerOnlyOk(requestCode, activityResultCallback)
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)
}

fun BaseActivity?.startActivityForResult(intent: Intent,
                                         options: Bundle?,
                                         requestCode: Int,
                                         activityResultCallback: ActivityResultCallback) {
    if (this == null) {
        logBadActivity()
        return
    }
    addActivityResultListener(requestCode, activityResultCallback)
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)
}


fun BaseActivity?.startActivityForResultAsync(intent: Intent,
                                              options: Bundle?,
                                              requestCode: Int,
                                              activityResultCallback: AsyncActivityResultCallbackOk) {
    if (this == null) {
        logBadActivity()
        return
    }
    addActivityResultListenerOnlyOkAsync(requestCode, activityResultCallback)
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)
}

fun BaseActivity?.startActivityForResultAsync(intent: Intent,
                                              options: Bundle?,
                                              requestCode: Int,
                                              activityResultCallback: AsyncActivityResultCallback) {
    if (this == null) {
        logBadActivity()
        return
    }
    addActivityResultListenerAsync(requestCode, activityResultCallback)
    ActivityCompat.startActivityForResult(this, intent, requestCode, options)
}
//</editor-fold>

//<editor-fold desc="Base fragment hookpoints">
fun BaseFragment?.startActivityForResult(intent: Intent,
                                         options: Bundle?,
                                         requestCode: Int,
                                         activityResultCallback: ActivityResultCallbackOk) {
    if (this == null) {
        logBadFragment()
        return
    }
    addActivityResultListenerOnlyOk(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseFragment?.startActivityForResult(intent: Intent,
                                         options: Bundle?,
                                         requestCode: Int,
                                         activityResultCallback: ActivityResultCallback) {
    if (this == null) {
        logBadFragment()
        return
    }
    addActivityResultListener(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}


fun BaseFragment?.startActivityForResultAsync(intent: Intent,
                                              options: Bundle?,
                                              requestCode: Int,
                                              activityResultCallback: AsyncActivityResultCallbackOk) {
    if (this == null) {
        logBadFragment()
        return
    }
    addActivityResultListenerOnlyOkAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseFragment?.startActivityForResult(intent: Intent,
                                         options: Bundle?,
                                         requestCode: Int,
                                         activityResultCallback: AsyncActivityResultCallback) {
    if (this == null) {
        logBadFragment()
        return
    }
    addActivityResultListenerAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}
//</editor-fold>

private fun logBadActivity() {
    L.error(BaseFragment::class,
            "Could not start activity for result, as the activity is properly not a BaseActivity",
            Exception())
}

private fun logBadFragment() {
    L.error(BaseFragment::class, "Could not start activity for result, as the caller is null",
            Exception())
}