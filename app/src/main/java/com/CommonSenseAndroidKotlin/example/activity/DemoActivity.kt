package com.CommonSenseAndroidKotlin.example.activity

import android.view.LayoutInflater
import com.CommonSenseAndroidKotlin.example.databinding.DemoActivityBinding
import com.CommonSenseAndroidKotlin.example.fragments.SimpleRecyclerDemo
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


open class DemoActivity : BaseDatabindingActivity<DemoActivityBinding>() {
    override fun createBinding(inflater: LayoutInflater): DemoActivityBinding = DemoActivityBinding.inflate(inflater)
    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SimpleRecyclerDemo())
    }


}