package csense.android.exampleApp.fragments

import android.view.*
import androidx.databinding.*
import androidx.recyclerview.widget.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.databinding.fragments.*
import com.commonsense.android.kotlin.views.features.*
import csense.android.exampleApp.databinding.*

/**
 * Created by Kasper Tvede on 24-06-2017.
 */
class SectionSwipeAdapterFragment : BaseDatabindingFragment<DemoRecyclerSectionSwipeBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerSectionSwipeBinding>
            = DemoRecyclerSectionSwipeBinding::inflate

    val adapter = BaseSearchableDataBindingRecyclerAdapter<String>()


    override fun useBinding() {
        launchInBackground("useBinding") {
            val items = (0 until 100).map { SimpleSwipeItem("") { adapter.removeAt(it, 0) } }
            launchInUi("useBindingInside") {
                adapter.addAll(items,0)
                adapter.attachSwipeFeature(binding.demoRecyclerSearchableRecyclerview.recyclerView)
                binding.demoRecyclerSearchableRecyclerview.setupAsync(adapter, LinearLayoutManager(context), this::refresh)
            }
        }

    }

    fun refresh() = launchInBackground("refresh") {

    }

}


class SimpleSwipeItem(value: String, val deleteAction: () -> Unit) :
        BaseSearchRenderModel<String, SimpleSwipeItemBinding, String>(value, SimpleSwipeItemBinding::class.java),
        SwipeableItem {
    override fun startView(binding: ViewDataBinding): View? {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return null
        return ourBinding.simpleSwipeItemDelete
    }

    override fun endView(binding: ViewDataBinding): View? {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return null
        return ourBinding.simpleSwipeItemSolve
    }

    override fun floatingView(binding: ViewDataBinding): View {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return binding.root
        return ourBinding.simpleSwipeItemContent
    }

    override fun onSwiped(direction: Direction, viewModel: ViewDataBinding) {
        deleteAction()
    }

    override fun isAcceptedByFilter(value: String): Boolean {
        return item.contains(value, ignoreCase = true)
    }

    override fun renderFunction(view: SimpleSwipeItemBinding, model: String, viewHolder: BaseViewHolderItem<SimpleSwipeItemBinding>) {
//        view.simpleSwipeItemContent.translationX = 0f
//        view.simpleSwipeItemContent.visibility = View.VISIBLE
//
//        view.simpleSwipeItemDelete.visibility = View.GONE
//        view.simpleSwipeItemSolve.visibility = View.GONE
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleSwipeItemBinding>
            = SimpleSwipeItemBinding::inflate


}