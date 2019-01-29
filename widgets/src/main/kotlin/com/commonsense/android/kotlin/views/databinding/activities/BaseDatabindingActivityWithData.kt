package com.commonsense.android.kotlin.views.databinding.activities

import androidx.databinding.*
import com.commonsense.android.kotlin.system.base.*

/**
 * Created by kasper on 25/07/2017.
 */
abstract class BaseDatabindingActivityWithData<out view : ViewDataBinding, out Input>
    : BaseActivityData<Input>(), Databindable<view> {

    override val binding: view by lazy {
        createBinding().invoke(layoutInflater)
    }

    /**
     * if we have all required data then we can setup the view and use the binding :)
     */
    override fun onSafeData() {
        setContentView(binding.root)
        binding.executePendingBindings()
        useBinding()
    }
}