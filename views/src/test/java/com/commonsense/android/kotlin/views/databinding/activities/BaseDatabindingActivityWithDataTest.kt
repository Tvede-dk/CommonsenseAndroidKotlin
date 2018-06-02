package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import org.junit.Test
import org.mockito.Mockito.mock

class BaseDatabindingActivityWithDataTest : BaseRoboElectricTest() {


    @Test
    fun testDataBindingBadData() {

    }
}


class TestDatabindingActWithData : BaseDatabindingActivityWithData<ViewDataBinding, String>() {
    override fun createBinding(): InflaterFunctionSimple<ViewDataBinding> {
        return { layoutInflater ->
            mock(ViewDataBinding::class.java)
        }
    }

    override fun useBinding() {

    }

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
