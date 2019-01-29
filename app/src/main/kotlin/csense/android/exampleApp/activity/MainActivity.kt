package csense.android.exampleApp.activity


import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.R
import csense.android.exampleApp.databinding.*
import csense.android.exampleApp.views.dataAware.*
import csense.android.exampleApp.views.tools.*
import csense.android.exampleApp.views.widgets.*


class MainActivity : BaseDatabindingActivity<MainActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<MainActivityBinding> =
            MainActivityBinding::inflate

    val adapter by lazy {
        BaseDataBindingRecyclerAdapter(this)
    }


    override fun useBinding() {
        setupAdapter()
        val manager = GridLayoutManager(this, 2)
        binding.mainActivityRecyclerView.setup(adapter, manager)
    }

    private fun setupAdapter() {
        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_tools) {
            startActivity(ToolsOverviewActivity::class)
        }, 0)
        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_adapter) {
            startActivity(WidgetsRecyclerExampleActivity::class)
        }, 0)
        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_base_activity) {
            startActivity(ToolsOverviewActivity::class)
        }, 0)

        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_base_fragment) {
            startActivity(ToolsOverviewActivity::class)
        }, 0)

        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_widgets) {
            startActivity(WidgetsOverviewActivity::class)
        }, 0)
        adapter.add(CategoryRecyclerRender(R.string.intro_text) {
            startActivity(DataAActivity::class)
        }, 0)


//        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_activitydata, {
//            startActivity(StartActivityData::class)
//        }), 0)

//        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_crypto, {
//            startActivity(MainCryptoActivity::class)
//        }), 0)
        //categories:
        // 1) tools
        // - crash
        // - anr
        // - fps
        // - lifecycle events listener
        // - ...
        // -

        // 2) views
        // - base databinding examples (activity / fragments)
        // - base databinding recycler adapter
        // - base databinding listView
        // -
        // -
        // 3) advanced concepts
        // - activity with data
        // -
        logError(toPrettyString())
    }

}

data class CategoryRenderData(@StringRes val text: Int, val onclick: EmptyFunction)

class CategoryRecyclerRender(@StringRes text: Int, onclick: EmptyFunction)
    : BaseRenderModel<CategoryRenderData, CategoryRenderViewBinding>(CategoryRenderData(text, onclick), CategoryRenderViewBinding::class) {

    override fun getInflaterFunction(): ViewInflatingFunction<CategoryRenderViewBinding> = CategoryRenderViewBinding::inflate

    override fun renderFunction(view: CategoryRenderViewBinding,
                                model: CategoryRenderData,
                                viewHolder: BaseViewHolderItem<CategoryRenderViewBinding>) {
        view.categoryRenderViewButton.setText(model.text)
        view.categoryRenderViewButton.setOnclickAsyncEmpty(model.onclick)
    }
}