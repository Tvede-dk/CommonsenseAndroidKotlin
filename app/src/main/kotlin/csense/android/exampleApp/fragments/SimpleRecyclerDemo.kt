package csense.android.exampleApp.fragments

import android.graphics.*
import android.support.v7.widget.*
import android.view.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.databinding.fragments.*
import com.commonsense.android.kotlin.views.extensions.*
import com.commonsense.android.kotlin.views.features.*
import com.commonsense.android.kotlin.views.features.SectionRecyclerTransaction.*
import csense.android.exampleApp.*
import csense.android.exampleApp.databinding.*

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
open class SimpleRecyclerDemo : BaseDatabindingFragment<DemoRecyclerSimpleViewBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerSimpleViewBinding> = DemoRecyclerSimpleViewBinding::inflate

    private val adapter by lazy {
        context?.let { BaseDataBindingRecyclerAdapter(it) }
    }

    override fun useBinding() {
        val adapter = adapter ?: return
        val context = context ?: return
        adapter.clear()
        for (section in 0 until 10) {
            for (i in 0 until 10) {
                adapter.add(SimpleListItemRender("First text is good text", section) {
                    val transaction = Builder(adapter).apply {
                        hideSection(section)
                        hideSection(section + 1)
                        hideSection(section + 2)
                    }.build()
                    transaction.applySafe()
                    showSnackbar(binding.root, R.string.app_name, R.string.app_name, 5000, onAction = {
                        transaction.resetSafe { logWarning("omg failure! list changed outside of modification") }
                    })
                }, section)
                adapter.add(SimpleListImageItemRender(Color.BLUE, section), section)
                adapter.add(SimpleListItemRender("Whats up test ?", section) {}, section)
                adapter.add(SimpleListImageItemRender(Color.RED, section), section)
            }
        }

        binding.demoRecyclerSimpleViewRecyclerview.setup(adapter, LinearLayoutManager(context))
        binding.demoRecyclerSimpleViewReset.setOnclickAsync {
            for (i in 0 until adapter.sectionCount) {
                adapter.showSection(i)
            }
        }
    }
}

fun setColorUsingBackground(view: View, section: Int) {
    val color = Color.rgb(section % 20 + 80, section % 50 + 50, section % 100 + 20)
    view.setBackgroundColor(color)
}


open class SimpleListItemRender(text: String, private val section: Int, private val callback: () -> Unit)
    : BaseRenderModel<String, SimpleListItemBinding>(text, SimpleListItemBinding::class.java) {
    override fun renderFunction(view: SimpleListItemBinding, model: String, viewHolder: BaseViewHolderItem<SimpleListItemBinding>) {
        view.simpleListText.text = "$model section - $section"
        view.root.setOnclickAsync { callback() }
        setColorUsingBackground(view.root, section)
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleListItemBinding> {
        return SimpleListItemBinding::inflate
    }

}

class SimpleListImageItemRender(color: Int, val section: Int) : BaseRenderModel<Int, SimpleListImageItemBinding>(color, SimpleListImageItemBinding::class.java) {
    override fun renderFunction(view: SimpleListImageItemBinding, model: Int, viewHolder: BaseViewHolderItem<SimpleListImageItemBinding>) {
        view.simpleListItemImageImage.colorFilter = PorterDuffColorFilter(model, PorterDuff.Mode.ADD)
        setColorUsingBackground(view.root, section)

    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleListImageItemBinding> = SimpleListImageItemBinding::inflate


}
