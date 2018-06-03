package com.commonsense.android.kotlin.system.base.helpers

import android.content.Intent
import com.commonsense.android.kotlin.system.dataFlow.ReferenceCountingMap
import com.commonsense.android.kotlin.test.*
import org.junit.Before
import org.junit.Test

/**
 * Created by Kasper Tvede on 01-06-2018.
 * Purpose:
 */
class BaseActivityWithDataTest : BaseRoboElectricTest() {

    @Before
    fun before() {
        getActivityDataMap().decrementCounter("0")
    }


    /**
     * Resemebles previous bugs
     */
    @Test
    fun closesWithBadDataNoIntent() {
        val actController = createActivityController<TestActWithData>()
        val act = actController.get().apply {
            intent = null
        }
        act.intent.assertNull("should be null for test")
        actController.create()
        act.gotSafeData.assert(false, "should not run bindings when data is missing ")
        act.gotBadData.assert(true, "Should run on bad data when data is bad / missing")
    }

    @Test
    fun closesWithBadDataNoIndex() {
        val actController = createActivityController<TestActWithData>()
        //actController.newIntent(Intent().putExtra("baseActivity-data-index", 0.toString()))
        val act = actController.create().get()
        act.intent.assertNotNullApply("should not be null for null") {
            hasExtra("baseActivity-data-index").assertFalse("should have intent but no index")
        }
        act.gotSafeData.assert(false, "should not run bindings when data is missing ")
        act.gotBadData.assert(true, "Should run on bad data when data is bad / missing")
    }

    @Test
    fun closesWithBadDataNoDataInMap() {
        val actController = createActivityController<TestActWithData>()
        actController.get().intent = Intent().putExtra("baseActivity-data-index", 0.toString())
        actController.create()
        actController.get().apply {
            intent.hasExtra("baseActivity-data-index").assertTrue("should have index")
            getActivityDataMap().hasItem("0").assertFalse("should not have item in map")
            gotSafeData.assertFalse("should not have binding since the map is empty")
            gotBadData.assertTrue("should have got bad data since, the map is empty")
        }
    }

    @Test
    fun worksWithData() {
        val actController = createActivityController<TestActWithData>()
        val intentAndIndex = BaseActivityData.createDataActivityIntent(
                context,
                TestActWithData::class,
                "some-data")
        actController.get().intent = intentAndIndex.intent
        actController.create()
        actController.get().apply {
            gotBadData.assertFalse("should have got good data")
            gotSafeData.assertTrue("should have got onSafeData.")
            data.assert("some-data")
        }
    }
}

class TestActWithData : BaseActivityData<String>() {
    var gotSafeData = false

    var gotBadData = false

    override fun beforeCloseOnBadData() {
        super.beforeCloseOnBadData()
        gotBadData = true
    }

    override fun onSafeData() {
        gotSafeData = true
    }
}

private fun getActivityDataMap(): ReferenceCountingMap {
    return BaseActivityData.dataReferenceMap
}