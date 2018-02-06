package com.commonsense.android.kotlin.system.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.commonsense.android.kotlin.system.base.helpers.TestReceiver
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assertSize
import org.junit.Test
import org.robolectric.Robolectric

/**
 * Created by kasper on 18/12/2017.
 */
class BaseActivityReceiverTest : BaseRoboElectricTest() {


    /**
     * verifies that unregister an listener behind the scenes does not break anything.
     */
    @Test
    fun testDoubleUnregisterBehindScenes() {
        val helper = Robolectric.buildActivity(ReceiverActivityTest::class.java).create().get()
        val test = TestReceiver()
        helper.registerReceiver(test, IntentFilter())
        helper.unregisterReceiverBehindScenes(test)
        helper.unregisterReceiver(test)
        helper.receiverHandler.listReceivers().assertSize(0,
                "should be empty and not have thrown at this point")
    }

    /**
     * due to the weird nature in android that registering twice is allowed, we are however not allowed
     * to unregister twice, which seems rather inconvenient and bad.
     * so the base activity makes sure we are not throwing when unregister.
     */
    @Test
    fun testUnregisterIsSafe() {
        val helper = Robolectric.buildActivity(ReceiverActivityTest::class.java).create().get()
        val test = TestReceiver()
        helper.unregisterReceiver(test)
        helper.receiverHandler.listReceivers().assertSize(0)
    }

    @Test
    fun testRegisteringAndUnRegistering() {
        val helper = Robolectric.buildActivity(ReceiverActivityTest::class.java).create().get()
        val test = TestReceiver()
        val test2 = TestReceiver()
        val test3 = TestReceiver()

        helper.registerReceiver(test, IntentFilter())
        helper.registerReceiver(test2, IntentFilter())
        helper.registerReceiver(test3, IntentFilter())
        helper.receiverHandler.listReceivers().assertSize(3)
        helper.unregisterReceiver(test)
        helper.unregisterReceiver(test2)
        helper.unregisterReceiver(test3)
        helper.receiverHandler.listReceivers().assertSize(0)
    }

    @Test
    fun testRegisteringAndDestroy() {
        val helper = Robolectric.buildActivity(ReceiverActivityTest::class.java).create()
        val test = TestReceiver()
        val test2 = TestReceiver()
        val test3 = TestReceiver()

        val act = helper.get()
        act.registerReceiver(test, IntentFilter())
        act.registerReceiver(test2, IntentFilter())
        act.registerReceiver(test3, IntentFilter())
        act.receiverHandler.listReceivers().assertSize(3)
        helper.destroy()
        act.receiverHandler.listReceivers().assertSize(0)

    }
}

private class ReceiverActivityTest : BaseActivity() {

    //bypasses the hooked unRegister receiver.
    fun unregisterReceiverBehindScenes(receiver: BroadcastReceiver) {
        super.unregisterReceiver(receiver)
    }
}
