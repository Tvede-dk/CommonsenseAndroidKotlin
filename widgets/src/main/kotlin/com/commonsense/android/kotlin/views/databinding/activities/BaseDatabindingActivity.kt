package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import com.commonsense.android.kotlin.system.base.BaseActivity

/**
 * created by Kasper Tvede
 * The basics of a databinding activity.
 *
 */

typealias InflaterFunctionSimple<T> = (layoutInflater: LayoutInflater) -> T

abstract class BaseDatabindingActivity<out T : ViewDataBinding> : BaseActivity(), Databindable<T> {

    override val binding: T by lazy {
        createBinding().invoke(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }
}