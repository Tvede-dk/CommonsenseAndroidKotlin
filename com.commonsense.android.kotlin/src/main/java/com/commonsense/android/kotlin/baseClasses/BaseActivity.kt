package com.commonsense.android.kotlin.baseClasses

import android.app.Activity
import android.content.Intent
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import com.commonsense.android.kotlin.android.PermissionsHandling
import com.commonsense.android.kotlin.android.extensions.transactionCommit
import com.commonsense.android.kotlin.android.extensions.transactionCommitNow
import com.commonsense.android.kotlin.android.logging.logError
import com.commonsense.android.kotlin.helperClasses.JobContainer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

/**
 * created by Kasper Tvede on 29-09-2016.
 */

interface ActivityResultCallback {
    fun onActivityResult(resultCode: Int, data: Intent?): Boolean
}

interface ActivityResultCallbackOk {
    fun onActivityResult(data: Intent?)
}

open class BaseActivity : AppCompatActivity() {

    val permissionHandler by lazy {
        PermissionsHandling()
    }

    private val activityResultListeners by lazy {
        SparseArray<ActivityResultCallback>()
    }

    private val localJobs by lazy {
        JobContainer()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionResult(requestCode, permissions, grantResults)
    }


    fun LaunchInBackground(group: String, action: suspend () -> Unit) {
        localJobs.performAction(CommonPool, action)
    }


    fun LaunchInUi(group: String, action: suspend () -> Unit) {
        localJobs.performAction(UI, action)
    }


    override fun onDestroy() {
        localJobs.cleanJobs()
        activityResultListeners.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultListeners[requestCode]?.onActivityResult(resultCode, data)
    }

    fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk) {
        addActivityResultListener(requestCode, object : ActivityResultCallback {
            override fun onActivityResult(resultCode: Int, data: Intent?): Boolean {
                if (resultCode == Activity.RESULT_OK) {
                    receiver.onActivityResult(data)
                }
                return true
            }
        })
    }

    fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback) {
        if (activityResultListeners[requestCode] != null && activityResultListeners[requestCode] != receiver) {
            //TODO should throw, warn, error ? fire missiles ??  this is a bad situation.. hmm
            logError("Overwriting an actual listener, for request code $requestCode")
            throw RuntimeException("Overwriting an actual listener, this is unsupported / not allowed behavior.")
        }
        activityResultListeners.put(requestCode, receiver)
    }

    fun removeActivityResultListener(requestCode: Int) {
        activityResultListeners.remove(requestCode)
    }

}

@AnyThread
fun Activity.safeFinish() = runOnUiThread(this::finish)


@UiThread
fun FragmentActivity.replaceFragment(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.transactionCommitNow {
        replace(container, fragment)
    }
}

@UiThread
fun FragmentActivity.pushNewFragmentTo(@IdRes container: Int, fragment: Fragment) {
    supportFragmentManager.transactionCommit {
        replace(container, fragment)
        addToBackStack(fragment.id.toString())
    }
}

