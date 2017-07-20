package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import com.commonsense.android.kotlin.system.base.BaseActivity

/**
 * created by Kasper Tvede on 29-09-2016.
 */

typealias InflaterFunctionSimple<T> = (layoutInflater: LayoutInflater) -> T

abstract class BaseDatabindingActivity<out T : ViewDataBinding> : BaseActivity() {

    val binding: T by lazy {
        createBinding().invoke(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }

    abstract fun useBinding()

    abstract fun createBinding(): InflaterFunctionSimple<T>
}