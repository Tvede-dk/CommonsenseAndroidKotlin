package csense.android.exampleApp.views.widgets.pagerAdapter

import android.os.*
import com.commonsense.android.kotlin.views.baseClasses.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*

class WidgetsBaseFragmentPagerAdapterActivity : BaseDatabindingActivity<WidgetsPagerAdapterExampleBinding>() {

    var internalCounter: Int = 0

    private val adapter by lazy {
        BaseFragmentPagerAdapter(supportFragmentManager)
    }

    override fun createBinding(): InflaterFunctionSimple<WidgetsPagerAdapterExampleBinding> =
            WidgetsPagerAdapterExampleBinding::inflate

    override fun useBinding() {
        binding.widgetsPagerExampleAddSingle.setOnclickAsync { addNew() }

        binding.widgetsPagerExampleAddMany.setOnclickAsync {
            for (i in 0 until 5) {
                addNew()
            }
        }

        binding.widgetsPagerExampleRemoveLast.setOnclickAsync { removeLast() }

        binding.widgetsPagerExampleRemoveFirst.setOnclickAsync { removeFirst() }

        binding.widgetsPagerExampleViewpager.setAdapterAndListener(adapter, BaseViewPagerOnChangeListener {
            binding.widgetsPagerExampleTitle.text = adapter.getPageTitle(it)
        })
    }

    private fun removeLast() = adapter.removeFragment(adapter.getAllFragments().last())

    private fun removeFirst() = adapter.removeFragment(adapter.getAllFragments().first())

    private fun addNew() = adapter.addFragment(
            WidgetsFragmentExample().apply {
                arguments = Bundle().apply {
                    putString("data", internalCounter.toString())
                }
            }, adapter.count.toString())

}