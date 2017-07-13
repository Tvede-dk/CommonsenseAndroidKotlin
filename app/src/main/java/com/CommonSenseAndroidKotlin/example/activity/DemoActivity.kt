package com.CommonSenseAndroidKotlin.example.activity

import com.CommonSenseAndroidKotlin.example.databinding.DemoActivityBinding
import com.CommonSenseAndroidKotlin.example.fragments.SimpleRecyclerDemo
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.databinding.InflaterFunctionSimple
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


open class DemoActivity : BaseDatabindingActivity<DemoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<DemoActivityBinding>
            = DemoActivityBinding::inflate

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SimpleRecyclerDemo())
    }


}