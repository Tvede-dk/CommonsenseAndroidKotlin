package com.commonsense.android.kotlin.system.permissions

import android.app.*
import android.content.*
import android.support.annotation.*
import android.support.v4.content.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import kotlinx.coroutines.*


//region Permission enum extensions
@UiThread
fun PermissionEnum.useIfPermitted(context: Context,
                                  usePermission: EmptyFunction,
                                  useError: EmptyFunction) {
    havePermission(context)
            .ifTrue(usePermission)
            .ifFalse(useError)
}

@UiThread
fun PermissionEnum.usePermission(handler: PermissionsHandling,
                                 activity: Activity,
                                 function: EmptyFunction,
                                 errorFunction: EmptyFunction) {
    handler.performActionForPermission(
            permissionValue,
            activity,
            function,
            errorFunction)
}

@UiThread
fun PermissionEnum.useSuspend(handler: PermissionsHandling,
                              activity: Activity,
                              function: AsyncEmptyFunction,
                              errorFunction: AsyncEmptyFunction) {
    handler.performActionForPermission(permissionValue, activity, {
        launchBlock(Dispatchers.Main, block = function)
    }, {
        launchBlock(Dispatchers.Main, block = errorFunction)
    })
}

@UiThread
fun PermissionEnum.useSuspend(handler: PermissionsHandling,
                              activity: BaseActivity,
                              function: AsyncFunctionUnit<Context>,
                              errorFunction: AsyncFunctionUnit<Context>) {
    handler.performActionForPermission(permissionValue, activity, {
        activity.launchInUi("PermissionEnum.useSuspend", function)
    }, {
        activity.launchInUi("PermissionEnum.useSuspend", errorFunction)
    })
}


@UiThread
inline fun PermissionEnum.usePermission(context: Context, crossinline usePermission: EmptyFunction) {
    havePermission(context).ifTrue(usePermission)
}

@UiThread
fun PermissionEnum.useSuspend(context: Context, usePermission: AsyncEmptyFunction): Job? {
    return if (havePermission(context)) {
        launchBlock(Dispatchers.Main, block = usePermission)
    } else {
        null
    }
}

@UiThread
fun PermissionEnum.useSuspend(context: BaseActivity, usePermission: AsyncFunctionUnit<Context>) {
    if (havePermission(context)) {
        context.launchInUi("PermissionEnum.useSuspend", usePermission)
    }
}


@UiThread
fun PermissionEnum.havePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permissionValue).isGranted()
}
//endregion


//region activity / fragment permissions extensions
/**
 * Asks iff necessary, for the use of the given permission
 * if allowed, the usePermission callback will be called
 * if not allowed the onFailed callback will be called
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 */
@UiThread
fun BaseFragment.usePermission(permission: PermissionEnum,
                               usePermission: EmptyFunction,
                               onFailed: EmptyFunction) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermission(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed()
    }

}

suspend fun BaseFragment.usePermissionSuspend(
        permission: PermissionEnum,
        usePermission: AsyncEmptyFunction,
        onFailed: AsyncEmptyFunction) {
    val act = baseActivity
    if (act != null) {
        act.usePermissionSuspend(permission, usePermission, onFailed)
    } else {
        onFailed()
    }
}

fun BaseActivity.usePermissionSuspend(
        permission: PermissionEnum,
        usePermission: AsyncEmptyFunction,
        onFailed: AsyncEmptyFunction) {
    permission.useSuspend(permissionHandler, this, usePermission, onFailed)
}

/**
 * Asks iff necessary, for the use of the given permission
 * if allowed, the usePermission callback will be called
 * if not allowed the onFailed callback will be called
 */
@UiThread
fun BaseActivity.usePermission(permission: PermissionEnum,
                               usePermission: EmptyFunction,
                               onFailed: EmptyFunction? = null) {
    permissionHandler.performActionForPermission(
            permission.permissionValue,
            this,
            usePermission,
            onFailed
                    ?: {})
}
//endregion
