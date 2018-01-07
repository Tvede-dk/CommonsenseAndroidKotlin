package com.CommonSenseAndroidKotlin.example.activity

import com.CommonSenseAndroidKotlin.example.fragments.SectionSwipeAdapterFragment
import com.commonsense.android.kotlin.system.extensions.replaceFragment

/**
 * Created by Kasper Tvede on 24-06-2017.
 */

class Demo5Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SectionSwipeAdapterFragment())
    }
}



