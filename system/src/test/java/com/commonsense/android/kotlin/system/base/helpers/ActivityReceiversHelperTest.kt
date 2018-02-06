package com.commonsense.android.kotlin.system.base.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.commonsense.android.kotlin.test.assertSize
import org.junit.Test

/**
 * Created by kasper on 15/12/2017.
 */
class ActivityReceiversHelperTest {


    @Test
    fun registerReceiver() {
        val test1 = TestReceiver()
        val test2 = TestReceiver()
        val helper = ActivityReceiversHelper()
        //verify that null never slips though.
        helper.registerReceiver(null)
        helper.listReceivers().assertSize(0, "null should never be stored")
        helper.registerReceiver(test1)
        helper.registerReceiver(test2)
        helper.listReceivers().assertSize(2, "should always add," +
                "even if same type or even same instance")
        helper.registerReceiver(test1)
        helper.listReceivers().assertSize(3, "should always add," +
                "even if same type or even same instance")

        helper.unregisterReceiver(test2)
        helper.listReceivers().assertSize(2, "should remove previous registered receiver")

        helper.unregisterReceiver(TestReceiver())
        helper.listReceivers().assertSize(2, "should not remove  when unknown receiver")

        helper.unregisterReceiver(test1)
        helper.listReceivers().assertSize(1)
        helper.unregisterReceiver(test1)
        helper.listReceivers().assertSize(0)
        //make sure that if null is not handled that we catch it here.
        helper.unregisterReceiver(null)

    }

    @Test
    fun onDestroy() {
        val test1 = TestReceiver()
        val test2 = TestReceiver()

        val helper = ActivityReceiversHelper()
        helper.registerReceiver(test1)
        helper.registerReceiver(test2)

    }

    @Test
    fun useIfEnabled() {
        val helper = ActivityReceiversHelper()
        helper.registerReceiver(TestReceiver())
        helper.registerReceiver(TestReceiver())

    }

    @Test
    fun testDestroyAfterAddRemove() {

    }

    /**
     * Calling unregister when its not registered behind the scenes because it was unregister behind the scenes
     */
    @Test
    fun testDoubleRemoveBehindScenes() {
        val test1 = TestReceiver()
        val helper = ActivityReceiversHelper()
        helper.registerReceiver(test1)
        helper.isEnabled = false
        helper.unregisterReceiver(test1)
        helper.isEnabled = true

    }

}

class TestReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}