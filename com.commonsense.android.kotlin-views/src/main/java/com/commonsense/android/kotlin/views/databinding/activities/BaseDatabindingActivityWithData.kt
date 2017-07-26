package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData

/**
 * Created by kasper on 25/07/2017.
 */
abstract class BaseDatabindingActivityWithData<out view : ViewDataBinding, out Input>
    : BaseActivityData<Input>(), Databindable<view> {

    override val binding: view by lazy {
        createBinding().invoke(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }


}