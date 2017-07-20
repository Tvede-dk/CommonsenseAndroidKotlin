package com.commonsense.android.kotlin.system.base

import android.app.Activity
import android.content.Intent
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.IntRange
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.commonsense.android.kotlin.base.scheduling.JobContainer
import com.commonsense.android.kotlin.system.PermissionsHandling
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.transactionCommit
import com.commonsense.android.kotlin.system.extensions.transactionCommitNow
import com.commonsense.android.kotlin.system.logging.logWarning
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

/**
 * created by Kasper Tvede on 29-09-2016.
 */


open class BaseActivity : AppCompatActivity(), ActivityResultHelperContainer {

    val permissionHandler by lazy {
        PermissionsHandling()
    }

    private val localJobs by lazy {
        JobContainer()
    }

    private val activityResultHelper by lazy {
        ActivityResultHelper({ logWarning(it) })
    }


    fun LaunchInBackground(group: String, action: suspend () -> Unit) {
        localJobs.performAction(CommonPool, action, group)
    }


    fun LaunchInUi(group: String, action: suspend () -> Unit) {
        localJobs.performAction(UI, action, group)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        localJobs.cleanJobs()
        activityResultHelper.clear()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHelper.handle(requestCode, resultCode, data)
    }

    //<editor-fold desc="Add activity result listener">
    override fun addActivityResultListenerOnlyOk(requestCode: Int, receiver: ActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOk(requestCode, receiver)
    }

    override fun addActivityResultListener(requestCode: Int, receiver: ActivityResultCallback) {
        activityResultHelper.addForAllResults(requestCode, receiver)
    }

    override fun addActivityResultListenerOnlyOkAsync(requestCode: Int, receiver: AsyncActivityResultCallbackOk) {
        activityResultHelper.addForOnlyOkAsync(requestCode, receiver)
    }

    override fun addActivityResultListenerAsync(requestCode: Int, receiver: AsyncActivityResultCallback) {
        activityResultHelper.addForAllResultsAsync(requestCode, receiver)
    }

    override fun removeActivityResultListener(@IntRange(from = 0) requestCode: Int) {
        activityResultHelper.remove(requestCode)
    }
    //</editor-fold>
}

@AnyThread
fun Activity.safeFinish() = runOnUiThread(this::finish)


//<editor-fold desc="push / replace fragment">
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
//</editor-fold>

