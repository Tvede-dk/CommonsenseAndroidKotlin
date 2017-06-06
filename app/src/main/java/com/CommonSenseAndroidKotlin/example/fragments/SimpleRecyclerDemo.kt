package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerSimpleViewBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListImageItemBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingFragment
import com.commonsense.android.kotlin.baseClasses.databinding.BaseRenderModel
import com.commonsense.android.kotlin.baseClasses.databinding.InflatingFunction

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
open class SimpleRecyclerDemo : BaseDatabindingFragment<DemoRecyclerSimpleViewBinding>() {

    val adapter by lazy {
        BaseDataBindingRecyclerAdapter(context.applicationContext)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): DemoRecyclerSimpleViewBinding = DemoRecyclerSimpleViewBinding.inflate(inflater, parent, false)

    override fun useBinding() {
        adapter.clear()
        for (i in 0..200) {
            adapter.add(SimpleListItemRender("First"))
            adapter.add(SimpleListImageItemRender(Color.BLUE))
            adapter.add(SimpleListItemRender("Whats up"))
            adapter.add(SimpleListImageItemRender(Color.RED))
        }
        binding.demoRecyclerSimpleViewRecyclerview.setup(adapter, LinearLayoutManager(context.applicationContext))
    }

}


open class SimpleListItemRender(text: String) : BaseRenderModel<String, SimpleListItemBinding>(text, SimpleListItemBinding::class.java) {
    override fun getInflaterFunction(): InflatingFunction<SimpleListItemBinding> {
        return SimpleListItemBinding::inflate
    }

    override fun renderFunction(view: SimpleListItemBinding, model: String) {
        view.simpleListText.text = model
    }
}

class SimpleListImageItemRender(color: Int) : BaseRenderModel<Int, SimpleListImageItemBinding>(color, SimpleListImageItemBinding::class.java) {
    override fun getInflaterFunction(): InflatingFunction<SimpleListImageItemBinding> = SimpleListImageItemBinding::inflate


    override fun renderFunction(view: SimpleListImageItemBinding, model: Int) {
        view.simpleListItemImageImage.colorFilter = PorterDuffColorFilter(model, PorterDuff.Mode.ADD)
    }
}
