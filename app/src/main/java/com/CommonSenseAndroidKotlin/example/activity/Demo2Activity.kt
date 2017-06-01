package com.CommonSenseAndroidKotlin.example.activity

import com.CommonSenseAndroidKotlin.example.fragments.SearchAbleRecyclerDemo
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by kasper on 01/06/2017.
 */
class Demo2Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SearchAbleRecyclerDemo())
    }
}