package com.commonsense.android.kotlin.system

import android.app.Activity
import android.os.Bundle
import android.util.Base64
import com.commonsense.android.kotlin.test.*
import org.junit.Assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 28-03-2018.
 * Purpose: Test the activity storage class, responsible for handling the storage of activity specific things;
 * this includes "data" in activity with data, such that when android eg causes an empty process we can recover correctly.
 * or load callbacks back in.
 *
 */
class ActivityStorageTest : BaseRoboElectricTest() {


    @Test
    fun states() {
        val settings = ActivityStorage<Activity>(createActivity(), DeviceSettings(context, "testSettings"))
        //simulate a new flow
        settings.onCreate(null)
        settings.secretKey.assertNotNull("should create new key when non specified")
        settings.secretKey.assertNotNullApply {
            Base64.decode(this, 0).size.assertLargerOrEqualTo(16, "should have more than 16 * 8 bites keys / larger than 128 bit key")
        }
        val bundle = Bundle()
        settings.onSaveInstanceState(bundle)
        bundle.getString(settings.secretKeyIndex).assertNotNullAndEquals(settings.secretKey, "should save the key.")

        val newSettings = ActivityStorage<Activity>(createActivity(), DeviceSettings(context, "testSettings"))
        newSettings.onCreate(bundle)
        newSettings.secretKey.assertNotNullAndEquals(settings.secretKey, "should use the stored key.")
    }

    @Test
    fun testCrypto() {
        //first an empty start.
        val deviceSettings = DeviceSettings(context, "testSettings")
        val settings = ActivityStorage<Activity>(createActivity(), deviceSettings)
        settings.onCreate(null)

        settings.saveData("testIndex", "a very secret")

        val loaded = deviceSettings.loadSetting("testIndex", "missing")
        if (loaded == null) {
            failTest("did not store data;")
            return
        }

        loaded.assertContainsNot("a very secret", false, "should not contain plaintext")

        settings.decryptData(loaded)

        //store data
        //verify its not plaintext
        //verify that we can decrypt it back
        // then recreate a new settings
        //see that it can load the encrypted data back.


    }

}
