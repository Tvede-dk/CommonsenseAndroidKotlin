package com.commonsense.android.kotlin.android

import android.Manifest
import android.content.pm.PackageManager
import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.Semaphore

/**
 * Created by Kasper Tvede on 27-05-2017.
 */
@Ignore
class PermissionsHandlingTest : BaseRoboElectricTest() {


    @Test
    fun testPermissionFlowAccept() {
        val sem = Semaphore(0)
        val act = createActivity<AlwaysPermissionActivity>()

        act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, sem::release, {
            Assert.fail("should be granted in tests")
        })
        Assert.assertTrue(sem.tryAcquire())
    }

    @Test
    fun testPermissionFlowDenyAccept() {
        val sem = Semaphore(0)
        val act = createActivity<DenyPermissionActivity>()

        act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, sem::release, {
            Assert.fail("should be granted in tests")
        })

        callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, true)
        //simulate response from user
        Assert.assertTrue(sem.tryAcquire())
    }

    @Test
    fun testPermissionFlowDenyDeny() {
        val sem = Semaphore(0)
        val act = createActivity<DenyPermissionActivity>()

        act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, {
            Assert.fail("should not be granted in tests")
        }, sem::release)

        //simulate response from user
        callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, false)

        Assert.assertTrue(sem.tryAcquire())
    }

    @Test
    fun testPermissionFlowDenyMultipleDeny() {
        val sem = Semaphore(0)
        val act = createActivity<DenyPermissionActivity>()

        val listenerCount = 10
        for (i in 0..listenerCount) {
            act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, {
                Assert.fail("should not be granted in tests")
            }, sem::release)
        }
        callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, false)
        Assert.assertTrue(sem.tryAcquire(listenerCount))

    }

    @Test
    fun testPermissionFlowDenyMultipleAllow() {
        val sem = Semaphore(0)
        val act = createActivity<DenyPermissionActivity>()

        val listenerCount = 5
        for (i in 0..listenerCount) {
            act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, sem::release, {
                Assert.fail("should not be granted in tests")
            })
        }
        callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, true)
        Assert.assertTrue(sem.tryAcquire(listenerCount))

    }


    private fun callHandlerWith(handler: PermissionsHandling, @DangerousPermissionString permission: String, granted: Boolean) {
        val managerValue = if (granted) {
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
        handler.onRequestPermissionResult(handler.handlerRequestCode, arrayOf(permission), intArrayOf(managerValue))
    }


}

class AlwaysPermissionActivity : BaseActivity() {


    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int {
        return PackageManager.PERMISSION_GRANTED
    }
}

class DenyPermissionActivity : BaseActivity() {
    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int {
        return PackageManager.PERMISSION_DENIED
    }
}
