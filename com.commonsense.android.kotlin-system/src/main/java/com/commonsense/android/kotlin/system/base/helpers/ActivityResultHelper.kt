package com.commonsense.android.kotlin.system.base.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntRange
import android.util.SparseArray
import com.commonsense.android.kotlin.base.extensions.collections.ifNull
import com.commonsense.android.kotlin.base.extensions.weakReference
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.base.BaseFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by Kasper Tvede on 20-07-2017.
 */

typealias ActivityResultCallback = (resultCode: Int, data: Intent?) -> Unit
typealias ActivityResultCallbackOk = (data: Intent?) -> Unit

typealias AsyncActivityResultCallback = suspend (resultCode: Int, data: Intent?) -> Unit
typealias AsyncActivityResultCallbackOk = suspend (data: Intent?) -> Unit



class ActivityResultHelper(warningLogger: ((message: String) -> Unit)? = null) {


    private val weakLogger = warningLogger.weakReference()

    private val activityResultListeners by lazy {
        SparseArray<ActivityResultCallbackInterface>()
    }

    fun clear() {
        activityResultListeners.clear()
    }

    fun handle(requestCode: Int, resultCode: Int, data: Intent?) {
        val listener = activityResultListeners[requestCode]
        listener?.onActivityResult(resultCode, data)
        listener.ifNull {
            weakLogger?.get()?.invoke("Actual listener not found for request code $requestCode")
        }
    }

    fun remove(@IntRange(from = 0) requestCode: Int) {
        activityResultListeners.remove(requestCode)
    }

    fun addForAllResults(@IntRange(from = 0) requestCode: Int, receiver: ActivityResultCallback) {
        addReceiver(ActivityResultWrapper({ resultCode, data ->
            receiver(resultCode, data)
        }, requestCode, this::remove))
    }

    fun addForOnlyOk(@IntRange(from = 0) requestCode: Int, receiver: ActivityResultCallbackOk) {
        addForAllResults(requestCode, { resultCode: Int, data: Intent? ->
            if (resultCode.isOkResult()) {
                receiver(data)
            }
        })
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
            weakLogger?.get()?.invoke("Overwriting an actual listener, for request code ${receiver.requestCode}")
            throw RuntimeException("Overwriting an actual listener, this is unsupported / not allowed behavior.")
        }
        activityResultListeners.put(receiver.requestCode, receiver)
    }

}

private fun Int.isOkResult(): Boolean = this == Activity.RESULT_OK


//<editor-fold desc="Interfaces">
interface ActivityResultCallbackInterface {
    fun onActivityResult(resultCode: Int, data: Intent?)
    val requestCode: Int
}


interface ActivityResultHelperContainer {
    fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk)
    fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback)
    fun addActivityResultListenerOnlyOkAsync(requestCode: Int, receiver: AsyncActivityResultCallbackOk)
    fun addActivityResultListenerAsync(requestCode: Int, receiver: AsyncActivityResultCallback)
    fun removeActivityResultListener(@IntRange(from = 0) requestCode: Int)
}
//</editor-fold>


//<editor-fold desc="Wrappers">
class ActivityResultWrapper(val callback: (resultCode: Int, data: Intent?) -> Unit,
                            override val requestCode: Int,
                            val removerFunction: (requestCode: Int) -> Unit)
    : ActivityResultCallbackInterface {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        callback(resultCode, data)
        removerFunction(requestCode)
    }
}

class AsyncActivityResultWrapper(val callback: suspend (resultCode: Int, data: Intent?) -> Unit,
                                 override val requestCode: Int,
                                 val removerFunction: (requestCode: Int) -> Unit)
    : ActivityResultCallbackInterface {
    override fun onActivityResult(resultCode: Int, data: Intent?) {
        launch(UI) {
            callback(resultCode, data)
            removerFunction(requestCode)
        }
    }
}
//</editor-fold>


//<editor-fold desc="Base activity hook points">
fun BaseActivity.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: ActivityResultCallbackOk) {
    addActivityResultListenerOnlyOk(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseActivity.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: ActivityResultCallback) {
    addActivityResultListener(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}


fun BaseActivity.startActivityForResultAsync(intent: Intent,
                                             options: Bundle?,
                                             requestCode: Int,
                                             activityResultCallback: AsyncActivityResultCallbackOk) {
    addActivityResultListenerOnlyOkAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseActivity.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: AsyncActivityResultCallback) {
    addActivityResultListenerAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}
//</editor-fold>

//<editor-fold desc="Base fragment hookpoints">
fun BaseFragment.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: ActivityResultCallbackOk) {
    addActivityResultListenerOnlyOk(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseFragment.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: ActivityResultCallback) {
    addActivityResultListener(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}


fun BaseFragment.startActivityForResultAsync(intent: Intent,
                                             options: Bundle?,
                                             requestCode: Int,
                                             activityResultCallback: AsyncActivityResultCallbackOk) {
    addActivityResultListenerOnlyOkAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}

fun BaseFragment.startActivityForResult(intent: Intent,
                                        options: Bundle?,
                                        requestCode: Int,
                                        activityResultCallback: AsyncActivityResultCallback) {
    addActivityResultListenerAsync(requestCode, activityResultCallback)
    startActivityForResult(intent, requestCode, options)
}
//</editor-fold>

