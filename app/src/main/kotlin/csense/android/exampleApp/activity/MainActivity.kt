package csense.android.exampleApp.activity


import android.support.annotation.StringRes
import android.support.v7.widget.GridLayoutManager
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.system.extensions.startActivity
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import csense.android.exampleApp.views.tools.ToolsOverviewActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.databinding.adapters.BaseDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.BaseRenderModel
import com.commonsense.android.kotlin.views.databinding.adapters.BaseViewHolderItem
import com.commonsense.android.kotlin.views.extensions.setOnclickAsyncEmpty
import com.commonsense.android.kotlin.views.extensions.setup
import csense.android.exampleApp.R
import csense.android.exampleApp.databinding.CategoryRenderViewBinding
import csense.android.exampleApp.databinding.MainActivityBinding


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
            startActivity(ToolsOverviewActivity::class)
        }, 0)
        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_base_activity) {
            startActivity(ToolsOverviewActivity::class)
        }, 0)

        adapter.add(CategoryRecyclerRender(R.string.mainactivity_category_base_fragment) {
            startActivity(ToolsOverviewActivity::class)
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