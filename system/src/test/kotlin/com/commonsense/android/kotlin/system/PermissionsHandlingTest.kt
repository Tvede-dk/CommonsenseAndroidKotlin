package com.commonsense.android.kotlin.system

import android.*
import android.content.pm.*
import android.os.*
import android.os.Looper.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.permissions.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.robolectric.Shadows.*
import org.robolectric.annotation.*
import org.robolectric.shadows.*
import java.util.concurrent.*

/**
 *
 */

@Config(sdk = [23])
class PermissionsHandlingTest : BaseRoboElectricTest() {


    @Test
    fun testPermissionFlowAccept() {
        val sem = Semaphore(0)
        val act = createActivity<AlwaysPermissionActivity>()

        act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act, sem::release) { _, _ ->
            Assert.fail("should be granted in tests")
        }
        Assert.assertTrue(sem.tryAcquire())
    }

    @Test
    fun testPermissionFlowDenyAccept() {
        val act = createActivity<DenyPermissionActivity>()
        testCallbackWithSemaphore { sem ->
            act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act,
                    onGranted = {
                        Assert.fail("should NOT be granted")
                    }, onFailed = { _, _ ->
                sem.release()
            })

            callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, false)
        }
        //simulate response from user

        testCallbackWithSemaphore { sem ->
            act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act,
                    onGranted = {
                        sem.release()
                    },
                    onFailed = { _, _ ->
                        Assert.fail("should be granted")
                    })
            callHandlerWith(act.permissionHandler, Manifest.permission.CALL_PHONE, true)
        }
    }

    @Test
    fun testPermissionFlowDenyDeny() {
        val sem = Semaphore(0)
        val act = createActivity<DenyPermissionActivity>()

        act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act, {
            Assert.fail("should not be granted in tests")
        }, { _, _ ->
            sem.release()
        })

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
            act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act, {
                Assert.fail("should not be granted in tests")
            }, { _, _ ->
                sem.release()
            })
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
            act.permissionHandler.performActionForPermissions(listOf(Manifest.permission.CALL_PHONE), act, {
                Assert.fail("should not be granted in tests")
            }, { _, _ ->
                sem.release()
            })
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
        handler.onRequestPermissionResult(createActivity(), handler.handlerRequestCode, arrayOf(permission), intArrayOf(managerValue))
    }


}

class AlwaysPermissionActivity : BaseActivity() {


    override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
            PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {

    }
}

class DenyPermissionActivity : BaseActivity() {
    override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
            PackageManager.PERMISSION_DENIED

    override fun onCreate(savedInstanceState: Bundle?) {

    }
}
