package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.system.base.BaseActivity

/**
 * created by Kasper Tvede on 29-09-2016.
 */

typealias InflaterFunctionSimple<T> = (layoutInflater: LayoutInflater) -> T

typealias InflaterFunctionFull<T> = (layoutInflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean) -> T

abstract class BaseDatabindingActivity<out T : ViewDataBinding> : BaseActivity(), Databindable<T> {

    override val binding: T by lazy {
        createBinding().invoke(layoutInflater, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }
}