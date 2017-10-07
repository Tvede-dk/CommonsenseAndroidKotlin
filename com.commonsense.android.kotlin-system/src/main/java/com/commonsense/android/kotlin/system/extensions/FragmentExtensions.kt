package com.commonsense.android.kotlin.system.extensions

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.view.WindowManager
import com.commonsense.android.kotlin.system.base.BaseFragment
import com.commonsense.android.kotlin.system.base.helpers.AsyncActivityResultCallback
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.system.base.startActivityWithData
import kotlin.reflect.KClass

/**
 * Created by Kasper Tvede on 10-01-2017.
 */


fun Fragment.getParrentContainerId() = (view?.parent as? ViewGroup)?.id

fun Fragment.presentDialer(phoneNumber: String) {
    context.presentDialer(phoneNumber)
}

fun Fragment.getActivityAsCompat(): AppCompatActivity? {
    return activity as? AppCompatActivity
}

fun DialogFragment.dialogFillParentView() {
    // Get existing layout params for the window
    val params = dialog.window?.attributes
    // Assign window properties to fill the parent
    params?.width = WindowManager.LayoutParams.MATCH_PARENT
    params?.height = WindowManager.LayoutParams.MATCH_PARENT
    dialog.window?.attributes = params as android.view.WindowManager.LayoutParams
    // Call super onResume after sizing
}

fun Fragment.onBackPressed() {
    activity.onBackPressed()
}

fun android.app.Fragment.onBackPressed() {
    activity.onBackPressed()
}

fun Fragment.popToFirstFragment() {
    activity.runOnUiThread {
        activity.supportFragmentManager.let { manager ->
            //first schedual a pop on "each" except the last. (n-1 pops)
            (0 until manager.backStackEntryCount).forEach { count -> manager.popBackStack() }
            manager.executePendingTransactions() //then allow Android to do the popping.
        }
    }
}


fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, optOnResult)
}


fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, optOnResult)
}

fun Fragment.finishActivity() {
    activity?.finish()
}