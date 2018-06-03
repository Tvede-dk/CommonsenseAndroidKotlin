package com.commonsense.android.kotlin.views.databinding.activities

import android.content.Intent
import android.databinding.ViewDataBinding
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.system.logging.PrintLogger
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertFalse
import com.commonsense.android.kotlin.test.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito.mock

class BaseDatabindingActivityWithDataTest : BaseRoboElectricTest() {

    @BeforeClass
    fun loggers() {
        PrintLogger.addToAllLoggers()
    }


    @Test
    fun testDataBindingNoDataIntent() {
        val controller = createActivityController<TestDatabindingActWithData>()
        controller.get().intent = null
        controller.create().get().apply {
            gotBadData.assertTrue("since there are no intent")
            gotSafeData.assertFalse("should not be called when no good data is there")
            gotBinding.assertFalse("should not have called binding on bad data")
        }
    }

    @Test
    fun testDataBindingNoData() {
        val controller = createActivityController<TestDatabindingActWithData>()
        controller.get().intent = Intent().putExtra("something", "value")
        controller.create().get().apply {
            gotBadData.assertTrue("since there are no intent")
            gotSafeData.assertFalse("should not be called when no good data is there")
            gotBinding.assertFalse("should not have called binding on bad data")
        }
    }

    @Test
    fun testDataBindingBadDataType() {
        val controller = createActivityController<TestDatabindingActWithData>()
        controller.get().intent = Intent().putExtra("something", "value")
        controller.create().get().apply {
            gotBadData.assertTrue("since there are no intent")
            gotSafeData.assertFalse("should not be called when no good data is there")
            gotBinding.assertFalse("should not have called binding on bad data")
        }
    }

    @Test
    fun testDatabindingOk() {
        val intentAndIndex = BaseActivityData.createDataActivityIntent(
                context,
                TestDatabindingActWithData::class,
                "Some data")
        val controller = createActivityController<TestDatabindingActWithData>()
        controller.get().intent = intentAndIndex.intent
        controller.create()
        controller.get().apply {
            gotSafeData.assertTrue("")
            gotBadData.assertFalse("")
            data.assert("Some data")
        }
    }

}


class TestDatabindingActWithData : BaseDatabindingActivityWithData<ViewDataBinding, String>() {

    var gotSafeData = false

    var gotBadData = false

    var gotBinding = false

    override fun createBinding(): InflaterFunctionSimple<ViewDataBinding> {
        return { layoutInflater ->
            mock(ViewDataBinding::class.java)
        }
    }

    override fun useBinding() {
        gotBinding = true
    }


    override fun beforeCloseOnBadData() {
        super.beforeCloseOnBadData()
        gotBadData = true
    }

    override fun onSafeData() {
        gotSafeData = true
        super.onSafeData()
    }

    /**
     * avoid setting any real view
     */
    override fun setContentView(view: View?) {

    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {

    }
}
