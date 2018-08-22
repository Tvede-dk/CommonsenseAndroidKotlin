package csense.android.exampleApp.views.widgets

import android.support.v7.widget.LinearLayoutManager
import com.commonsense.android.kotlin.base.extensions.collections.repeateToSize
import com.commonsense.android.kotlin.base.extensions.type
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.databinding.adapters.BaseDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.BaseRenderModel
import com.commonsense.android.kotlin.views.databinding.adapters.BaseViewHolderItem
import com.commonsense.android.kotlin.views.databinding.adapters.hideSections
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import csense.android.exampleApp.databinding.WidgetsRecyclerExampleBinding
import csense.android.exampleApp.databinding.WidgetsRecyclerItemExampleBinding

class WidgetsRecyclerExampleActivity : BaseDatabindingActivity<WidgetsRecyclerExampleBinding>() {

    private val adapter = BaseDataBindingRecyclerAdapter(this)

    override fun createBinding(): InflaterFunctionSimple<WidgetsRecyclerExampleBinding> =
            WidgetsRecyclerExampleBinding::inflate

    override fun useBinding() {
        binding.widgetsRecyclerExampleRecycler.setup(adapter, LinearLayoutManager(this)) {

        }
        binding.widgetsRecyclerExampleAdd1000.setOnclickAsync {
            val mapped = listOf(ViewRender("0")).repeateToSize(1000)
            adapter.addAll(mapped, 0)
        }

        binding.widgetsRecyclerExampleAdd10000.setOnclickAsync {
            for (i in 0 until 1000) {
                val section = i * 5
                val mapped = listOf(ViewRender("$section")).repeateToSize(10)
                adapter.addAll(mapped, section)
            }
        }

        binding.widgetsRecyclerExampleClear.setOnclickAsync {
            adapter.clear()
        }

        binding.widgetsRecyclerExampleRemove1000.setOnclickAsync {
//            adapter.smoothScrollToSection()
            adapter.hideSections(5,10,15)
        }


    }
}

class ViewRender(text: String) : BaseRenderModel<String, WidgetsRecyclerItemExampleBinding>(text, type()) {
    override fun renderFunction(view: WidgetsRecyclerItemExampleBinding,
                                model: String,
                                viewHolder: BaseViewHolderItem<WidgetsRecyclerItemExampleBinding>) {

        view.widgetsRecyclerExampleItemText.text = model
    }

    override fun getInflaterFunction(): ViewInflatingFunction<WidgetsRecyclerItemExampleBinding> =
            WidgetsRecyclerItemExampleBinding::inflate

}