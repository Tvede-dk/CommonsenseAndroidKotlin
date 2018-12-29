package com.commonsense.android.kotlin.system

import android.*
import android.content.pm.*
import android.os.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.permissions.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.robolectric.annotation.*
import java.util.concurrent.*

/**
 * Created by Kasper Tvede on 27-05-2017.
 */

@Config(sdk = [23])
class PermissionsHandlingTest : BaseRoboElectricTest() {


    @Test
    fun testPermissionFlowAccept() {
        val sem = Semaphore(0)
        val act = createActivity<AlwaysPermissionActivity>()

        act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, sem::release) {
            Assert.fail("should be granted in tests")
        }
        Assert.assertTrue(sem.tryAcquire())
    }

    @Test
    fun testPermissionFlowDenyAccept() {
        val act = createActivity<DenyPermissionActivity>()
        testCallbackWithSemaphore { sem ->
            act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, {
                Assert.fail("should be granted in tests")
            }, sem::release)

            callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, false)
        }
        //simulate response from user
        testCallbackWithSemaphore { sem ->
            act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act,
                    { sem.release() },
                    { Assert.fail("should be granted in tests") })

            callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, true)
        }
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
        for (i in 0 until listenerCount) {
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
        for (i in 0 until listenerCount) {
            act.permissionHandler.performActionForPermission(Manifest.permission.CALL_PHONE, act, {
                Assert.fail("should not be granted in tests")
            }, sem::release)
        }
        callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, false)
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

    @Ignore
    @Test
    fun performActionForPermission() {
    }

    @Ignore
    @Test
    fun onRequestPermissionResult() {
    }

    @Ignore
    @Test
    fun requestPermissions() {
    }

    @Ignore
    @Test
    fun toPrettyString() {
    }

    @Ignore
    @Test
    fun getHandlerRequestCode() {
    }


}

class AlwaysPermissionActivity : BaseActivity() {


    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int =
            PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {

    }
}

class DenyPermissionActivity : BaseActivity() {
    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int =
            PackageManager.PERMISSION_DENIED

    override fun onCreate(savedInstanceState: Bundle?) {

    }
}
