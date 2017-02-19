package com.commonsense.android.kotlin.baseClasses.databinding

import android.os.Bundle
import android.view.LayoutInflater
import com.commonsense.android.kotlin.baseClasses.BaseActivity

/**
 * Created by admin on 29-09-2016.
 */
abstract class BaseDatabindingActivity<out T : android.databinding.ViewDataBinding> : BaseActivity() {

    val binding: T by lazy {
        createBinding(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }

    abstract fun useBinding()

    abstract fun createBinding(inflater: LayoutInflater): T
}