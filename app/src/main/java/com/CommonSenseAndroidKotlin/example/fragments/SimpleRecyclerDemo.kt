package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerSimpleViewBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListImageItemBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.android.kotlin.baseClasses.databinding.*

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
open class SimpleRecyclerDemo : BaseDatabindingFragment<DemoRecyclerSimpleViewBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerSimpleViewBinding>
            = DemoRecyclerSimpleViewBinding::inflate

    private val adapter by lazy {
        BaseDataBindingRecyclerAdapter(context.applicationContext)
    }

    override fun useBinding() {
        adapter.clear()
        for (section in 0 until 10) {
            for (i in 0 until 10) {
                adapter.add(SimpleListItemRender("First text is good text", section, { adapter.hideSection(section) }), section)
                adapter.add(SimpleListImageItemRender(Color.BLUE, section), section)
                adapter.add(SimpleListItemRender("Whats up test ?", section, {}), section)
                adapter.add(SimpleListImageItemRender(Color.RED, section), section)
            }
        }

        binding.demoRecyclerSimpleViewRecyclerview.setup(adapter, LinearLayoutManager(context.applicationContext))
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


open class SimpleListItemRender(text: String, private val section: Int, private val callback: () -> Unit) : BaseRenderModel<String, SimpleListItemBinding>(text, SimpleListItemBinding::class.java) {
    override fun renderFunction(view: SimpleListItemBinding, model: String, viewHolder: BaseViewHolderItem<SimpleListItemBinding>) {
        view.simpleListText.text = model + " section - " + section
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
