package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import kotlin.reflect.KClass

/**
 * Created by kasper on 25/07/2017.
 */
abstract class BaseDatabindingActivityWithData<out view : ViewDataBinding, Input : Any>
    : BaseActivityData<Input>(), Databindable<view> {

    override val binding: view by lazy {
        createBinding().invoke(layoutInflater, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    override fun onDataReady() {
        binding.executePendingBindings()
        useBinding()
    }

}