package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.LinearLayoutManager
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
        for (section in 0..5) {
            for (i in 0..10) {
                adapter.add(SimpleListItemRender("First text is good text", section, { adapter.hideSection(section) }), section)
                adapter.add(SimpleListImageItemRender(Color.BLUE), section)
                adapter.add(SimpleListItemRender("Whats up test ?", section, {}), section)
                adapter.add(SimpleListImageItemRender(Color.RED), section)
            }
        }

        binding.demoRecyclerSimpleViewRecyclerview.setup(adapter, LinearLayoutManager(context.applicationContext))
    }

}


open class SimpleListItemRender(text: String, private val section: Int, private val callback: () -> Unit) : BaseRenderModel<String, SimpleListItemBinding>(text, SimpleListItemBinding::class.java) {
    override fun renderFunction(view: SimpleListItemBinding, model: String, viewHolder: BaseViewHolderItem<SimpleListItemBinding>) {
        view.simpleListText.text = model + " section - " + section
        view.root.setOnclickAsync { callback() }
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleListItemBinding> {
        return SimpleListItemBinding::inflate
    }

}

class SimpleListImageItemRender(color: Int) : BaseRenderModel<Int, SimpleListImageItemBinding>(color, SimpleListImageItemBinding::class.java) {
    override fun renderFunction(view: SimpleListImageItemBinding, model: Int, viewHolder: BaseViewHolderItem<SimpleListImageItemBinding>) {
        view.simpleListItemImageImage.colorFilter = PorterDuffColorFilter(model, PorterDuff.Mode.ADD)
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleListImageItemBinding> = SimpleListImageItemBinding::inflate


}
