package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.DataBindingComponent
import android.databinding.ViewDataBinding
import android.view.View
import com.commonsense.android.kotlin.BaseRoboElectricTest
import com.commonsense.android.kotlin.android.extensions.widets.removeLast
import com.commonsense.android.kotlin.collections.TypeLookupCollectionRepresentive
import com.commonsense.android.kotlin.extensions.collections.repeateToSize
import com.commonsense.android.kotlin.extensions.measureSecondTime
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.robolectric.RuntimeEnvironment

/**
 * Created by Kasper Tvede on 04-06-2017.
 */

class openAbstractRecycler(context: Context) : AbstractDataBindingRecyclerAdapter<RenderModelItem<*, *>>(context) {
    fun getData(): TypeLookupCollectionRepresentive<RenderModelItem<*, *>, InflatingFunction<ViewDataBinding>> {
        return dataCollection
    }
}

open class EmptyViewBinding : ViewDataBinding(object : DataBindingComponent {}, View(RuntimeEnvironment.application), 0) {
    override fun onFieldChange(localFieldId: Int, `object`: Any?, fieldId: Int) = false
    override fun invalidateAll() {}
    override fun hasPendingBindings() = false
    override fun executeBindings() {}
    override fun setVariable(variableId: Int, value: Any?) = false
}

class EmptyViewBinding2 : EmptyViewBinding()

class EmptyViewRenderModelItem(data: String) : RenderModelItem<String, EmptyViewBinding>(
        data,
        { inflater, parent, attach -> EmptyViewBinding() },
        EmptyViewBinding::class.java, { view, model -> })

class EmptyViewRenderModelItem2(data: Int) : RenderModelItem<Int, EmptyViewBinding2>(
        data,
        { inflater, parent, attach -> EmptyViewBinding2() },
        EmptyViewBinding2::
        class.java,
        { view, model -> })

class AbstractDataBindingRecyclerAdapterTest : BaseRoboElectricTest() {


    //validate some worst case performance things.

    @Test
    @Ignore
    fun testPerf() {
        val size = 200000
        val list = listOf(EmptyViewRenderModelItem("")).repeateToSize(size)
        val adapter = openAbstractRecycler(context)

        val timeBaseLine = measureSecondTime {
            adapter.removeAt(0)
        }
        val timelimit = 1 + timeBaseLine * 10

        val timeAddAll = measureSecondTime {
            adapter.addAll(list)
        }
        Assert.assertTrue(timeAddAll < timelimit)

        val timeRemoveFirst = measureSecondTime {
            adapter.removeAt(0)
        }
        Assert.assertTrue(timeRemoveFirst < timelimit)
        val timeRemoveMiddel = measureSecondTime {
            adapter.removeAt(size / 2)
        }
        Assert.assertTrue(timeRemoveMiddel < 1)
        val timeRemoveLast = measureSecondTime {
            adapter.removeAt(adapter.itemCount - 1)
        }
        Assert.assertTrue(timeRemoveLast < timelimit)

    }

    /**
     * Worst case pattern.
     */
    @Test
    @Ignore
    fun testPerformanceMultipleTypes() {
        val size = 2000000
        val list = listOf(EmptyViewRenderModelItem("")).repeateToSize(size)
        val adapter = openAbstractRecycler(context)
        adapter.add(EmptyViewRenderModelItem2(1))
        adapter.addAll(list)
        adapter.add(EmptyViewRenderModelItem2(1))

        val timeBaseLine = measureSecondTime {
            adapter.removeAt(0)
        }
        val timelimit = 1 + timeBaseLine * 10
        val timeRemoveFirst = measureSecondTime {
            adapter.removeAt(0)
        }
        Assert.assertTrue(timeRemoveFirst < timelimit)

        val timeLookup = measureSecondTime {
            adapter.getItemViewType(adapter.itemCount - 1)
        }
        Assert.assertTrue(timeLookup < timelimit)

        val timeRemoveLast = measureSecondTime {
            adapter.removeLast()
        }
        Assert.assertTrue(timeRemoveLast < timelimit)
    }

    @Test
    fun testDataFlow() {

    }
}
