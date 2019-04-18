package csense.android.exampleApp.views.widgets

import android.support.v7.widget.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.logging.logError
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*

class WidgetsRecyclerExampleActivity : BaseDatabindingActivity<WidgetsRecyclerExampleBinding>() {

    private val adapter = BaseDataBindingRecyclerAdapter(this)

    override fun createBinding(): InflaterFunctionSimple<WidgetsRecyclerExampleBinding> =
            WidgetsRecyclerExampleBinding::inflate

    override fun useBinding() {
        binding.widgetsRecyclerExampleRecycler.setup(adapter, LinearLayoutManager(this)) {

        }
        binding.widgetsRecyclerExampleAdd1.setOnclickAsync {
            add1()
        }

        binding.widgetsRecyclerExampleAdd2.setOnclickAsync {
            add2()
        }

        binding.widgetsRecyclerExampleClear.setOnclickAsync {
            clear()
        }

        binding.widgetsRecyclerExampleHide.setOnclickAsync {
            hide()
        }

        binding.widgetsRecyclerExampleTryAuto.setOnclickAsync {
            tryFun()
        }

    }

    //lets try some fun things out
    private fun tryFun() {
//        clear()
        add2()
        hide()
        add1()
//        hide()
        add2()
//        hide()
//        hide()
        add2()
        add2()
        launchInUi("tryFun") {
            adapter.smoothScrollToSection(19)
        }
        logError(adapter.toPrettyString())
    }

    private fun clear() {
        adapter.clear()
    }

    private fun hide() {
        adapter.hideSections(5, 10, 15)
    }

    private fun add2() {
        for (i in 0 until 20) {
            val mapped = listOf(ViewRender("section = $i")).repeateToSize(10)
            adapter.addAll(mapped, i)
        }
    }

    private fun add1() {
        val mapped = listOf(ViewRender("0")).repeateToSize(100)
        adapter.addAll(mapped, 0)
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