package com.commonsense.android.kotlin.views.widgets

import android.content.*
import android.databinding.*
import android.view.*
import com.commonsense.android.kotlin.test.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import org.junit.*
import org.robolectric.annotation.*

/**
 * Created by Kasper Tvede on 27-05-2017.
 */
@Config(manifest = Config.NONE)
class BaseDataBindingRecyclerViewTest : BaseRoboElectricTest() {


    @Test
    fun testLookup() {
        val recycler = BaseDataBindingRecyclerAdapter(context)
        recycler.add(createVm1RenderModel(), 0)
        Assert.assertNotEquals(recycler.getItemViewType(0), 0)
        recycler.add(createVm1RenderModel(), 0)
        Assert.assertNotEquals(recycler.getItemViewType(1), 0)
        //validate the equality across types.
        Assert.assertEquals(recycler.getItemViewType(0), recycler.getItemViewType(1))

        val firstVm1Model1TypeCode = recycler.getItemViewType(0)


        //now mix in another type.
        val vm2Model1 = createVm2RenderModel()
        val vm2Model2 = createVm2RenderModel()

        recycler.add(vm2Model1, 0)
        recycler.add(vm2Model2, 0)
        Assert.assertNotEquals(recycler.getItemViewType(0), recycler.getItemViewType(2))
        Assert.assertNotEquals(recycler.getItemViewType(1), recycler.getItemViewType(3))
        //make sure that rendervm2's are the same type
        Assert.assertEquals(recycler.getItemViewType(2), recycler.getItemViewType(3))

        recycler.removeAt(3, 0)
        recycler.removeAt(2, 0)

        //now vm2 model is out, make sure that the type model are still correct.
        Assert.assertEquals(firstVm1Model1TypeCode, recycler.getItemViewType(0))

    }


    private fun createVm2RenderModel(): RenderModel<String, TestVm2> {
        return RenderModel("",
                { _, _, _ -> TestVm2(context) },
                TestVm2::class.java, { _, _, _ -> })
    }

    private fun createVm1RenderModel(): RenderModel<String, TestVm1> {
        return RenderModel("",
                { _, _, _ -> TestVm1(context) },
                TestVm1::class.java, { _, _, _ -> })
    }

}

open class TestVm1(context: Context) : ViewDataBinding(object : DataBindingComponent {}, View(context), 0) {
    override fun setVariable(variableId: Int, value: Any?): Boolean {
        return true
    }

    override fun executeBindings() {
    }

    override fun onFieldChange(localFieldId: Int, `object`: Any?, fieldId: Int): Boolean {
        return true
    }

    override fun invalidateAll() {
    }

    override fun hasPendingBindings(): Boolean {
        return true
    }

}

class TestVm2(context: Context) : TestVm1(context)