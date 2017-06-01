package com.CommonSenseAndroidKotlin.example.activity


import android.content.Intent
import android.view.LayoutInflater
import com.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.commonsense.android.kotlin.android.extensions.widets.setOnClick
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity


class MainActivity : BaseDatabindingActivity<ActivityMainBinding>() {

    override fun useBinding() {

        binding.activityMainBasicRecyclerDemo.setOnClick {
            startActivity(Intent(applicationContext, DemoActivity::class.java))
        }
        binding.activityMainAdvanceRecyclerDemo.setOnClick {
            startActivity(Intent(applicationContext, Demo2Activity::class.java))
        }


    }

    override fun createBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)


}

