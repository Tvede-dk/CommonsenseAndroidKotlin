package com.CommonSenseAndroidKotlin.example.activity

import com.CommonSenseAndroidKotlin.example.fragments.EditDatabindingFragment
import com.commonsense.android.kotlin.system.extensions.replaceFragment

/**
 * Created by kasper on 01/06/2017.
 */
class Demo4Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, EditDatabindingFragment())
    }
}