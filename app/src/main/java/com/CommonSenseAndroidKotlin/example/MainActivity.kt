package com.CommonSenseAndroidKotlin.example


import android.content.Intent
import android.view.LayoutInflater
import com.CommonSenseAndroidKotlin.example.activity.DemoActivity
import com.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.commonsense.android.kotlin.android.extensions.widets.setOnClick
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDataBindingRecyclerView
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity


class MainActivity : BaseDatabindingActivity<ActivityMainBinding>() {
    private val adapter by lazy {
        BaseDataBindingRecyclerView(this.applicationContext)
    }

    override fun useBinding() {
        binding.mainTitle.setOnClick {
            startActivity(Intent(this@MainActivity, DemoActivity::class.java))
        }
    }

    override fun createBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)


}

