@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.databinding.activities

import android.content.*
import android.databinding.*
import android.view.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.mockito.Mockito.*

class BaseDatabindingActivityWithDataTest : BaseRoboElectricTest() {

    companion object {
        @BeforeClass
        fun loggers() {
            PrintLogger.addToAllLoggers()
        }
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
                null,
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

    @Ignore
    @Test
    fun getBinding() {
    }

    @Ignore
    @Test
    fun onSafeData() {
    }

}


class TestDatabindingActWithData : BaseDatabindingActivityWithData<ViewDataBinding, String>() {

    var gotSafeData = false

    var gotBadData = false

    var gotBinding = false

    override fun createBinding(): InflaterFunctionSimple<ViewDataBinding> {
        return { _ ->
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
