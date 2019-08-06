package com.commonsense.android.kotlin.system.permissions

import android.content.Context
import androidx.annotation.UiThread
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.extensions.collections.ifFalse
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.base.BaseFragment


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
inline fun PermissionEnum.usePermission(context: Context, usePermission: EmptyFunction) {
    havePermission(context).ifTrue(usePermission)
}

@UiThread
fun PermissionEnum.havePermission(context: Context): Boolean =
        context.havePermission(permissionValue)
//endregion


//region activity / fragment permissions extensions


//region Base activity permission enum
/**
 * Asks iff necessary, for the use of the given permission
 * if allowed, the usePermission callback will be called
 * if not allowed the onFailed callback will be called
 */
@UiThread
fun BaseActivity.usePermissionEnum(permission: PermissionEnum,
                                   usePermission: EmptyFunction,
                                   onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsEnum(
            listOf(permission),
            this,
            usePermission,
            onFailed)
}

@UiThread
fun BaseActivity.usePermissionEnumFull(permission: PermissionEnum,
                                       usePermission: PermissionsSuccessCallback,
                                       onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsEnumFull(
            listOf(permission),
            this,
            usePermission,
            onFailed)
}

@UiThread
fun BaseActivity.usePermissionEnums(permissions: List<PermissionEnum>,
                                    usePermission: EmptyFunction,
                                    onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsEnum(
            permissions,
            this,
            usePermission,
            onFailed)
}

@UiThread
fun BaseActivity.usePermissionEnumsFull(permissions: List<PermissionEnum>,
                                        usePermission: PermissionsSuccessCallback,
                                        onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsEnumFull(
            permissions,
            this,
            usePermission,
            onFailed)
}
//endregion

//region Base activity permission string
@UiThread
fun BaseActivity.usePermission(permission: @DangerousPermissionString String,
                               usePermission: EmptyFunction,
                               onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissions(
            listOf(permission),
            this,
            usePermission,
            onFailed)
}


@UiThread
fun BaseActivity.usePermissionFull(permission: @DangerousPermissionString String,
                                   usePermission: PermissionsSuccessCallback,
                                   onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsFull(
            listOf(permission),
            this,
            usePermission,
            onFailed)
}

@UiThread
fun BaseActivity.usePermissions(permissions: List<@DangerousPermissionString String>,
                                usePermission: EmptyFunction,
                                onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissions(
            permissions.toList(),
            this,
            usePermission,
            onFailed)
}


@UiThread
fun BaseActivity.usePermissionsFull(permissions: List<@DangerousPermissionString String>,
                                    usePermission: PermissionsSuccessCallback,
                                    onFailed: PermissionsFailedCallback? = null) {
    permissionHandler.performActionForPermissionsFull(
            permissions.toList(),
            this,
            usePermission,
            onFailed)
}
//endregion

//region Base fragment use permission enum
/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission PermissionEnum
 * @param usePermission EmptyFunction
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionEnum(permission: PermissionEnum,
                                   usePermission: EmptyFunction,
                                   onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionEnum(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), listOf(permission.permissionValue))
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission PermissionEnum
 * @param usePermission PermissionsSuccessCallback
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionEnumFull(permission: PermissionEnum,
                                       usePermission: PermissionsSuccessCallback,
                                       onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionEnumFull(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), listOf(permission.permissionValue))
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permissions List<PermissionEnum>
 * @param usePermission EmptyFunction
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionEnums(permissions: List<PermissionEnum>,
                                    usePermission: EmptyFunction,
                                    onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionEnums(
                permissions,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), permissions.map { it.permissionValue })
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permissions List<PermissionEnum>
 * @param usePermission PermissionsSuccessCallback
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionEnumsFull(permissions: List<PermissionEnum>,
                                        usePermission: PermissionsSuccessCallback,
                                        onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionEnumsFull(
                permissions,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), permissions.map { it.permissionValue })
    }
}
//endregion

//region base fragment use permission string
/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission @DangerousPermissionString String
 * @param usePermission EmptyFunction
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermission(permission: @DangerousPermissionString String,
                               usePermission: EmptyFunction,
                               onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermission(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), listOf(permission))
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission @DangerousPermissionString String
 * @param usePermission PermissionsSuccessCallback
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionFull(permission: @DangerousPermissionString String,
                                   usePermission: PermissionsSuccessCallback,
                                   onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionFull(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), listOf(permission))
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission List<@DangerousPermissionString String>
 * @param usePermission EmptyFunction
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissions(permission: List<@DangerousPermissionString String>,
                                usePermission: EmptyFunction,
                                onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissions(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), permission)
    }
}

/**
 *
 * NB if the activity IS NOT A BASEACTIVITY then onfailed will be called
 * @receiver BaseFragment
 * @param permission List<@DangerousPermissionString String>
 * @param usePermission PermissionsSuccessCallback
 * @param onFailed PermissionsFailedCallback?
 */
@UiThread
fun BaseFragment.usePermissionsFull(permission: List<@DangerousPermissionString String>,
                                    usePermission: PermissionsSuccessCallback,
                                    onFailed: PermissionsFailedCallback?) {
    val baseAct = baseActivity
    if (baseAct != null) {
        baseAct.usePermissionsFull(
                permission,
                usePermission,
                onFailed)
    } else {
        onFailed?.invoke(emptyList(), permission)
    }
}
//endregion


//endregion
