package com.CommonSenseAndroidKotlin.example.activity


import android.content.Intent
import com.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.CommonSenseAndroidKotlin.example.fragments.EditDatabindingFragment
import com.commonsense.android.kotlin.android.extensions.widets.setOnClick
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.databinding.InflaterFunctionSimple


class MainActivity : BaseDatabindingActivity<ActivityMainBinding>() {
    override fun createBinding(): InflaterFunctionSimple<ActivityMainBinding>
            = ActivityMainBinding::inflate

    override fun useBinding() {

        binding.activityMainBasicRecyclerDemo.setOnClick {
            startActivity(Intent(applicationContext, DemoActivity::class.java))
        }
        binding.activityMainAdvanceRecyclerDemo.setOnClick {
            startActivity(Intent(applicationContext, Demo2Activity::class.java))
        }
        binding.activityMainAdvanceRecyclerDemoFastScroller.setOnClick {
            startActivity(Intent(applicationContext, Demo3Activity::class.java))
        }
        binding.activityMainDemo4.setOnClick {
            startActivity(Intent(applicationContext, Demo4Activity::class.java))
        }
        binding.activityMainDemo5.setOnClick {
            startActivity(Intent(applicationContext, Demo5Activity::class.java))
        }

        binding.activityMainAdvanceAsyncButton.setOnclickAsync {
            EditDatabindingFragment().show(supportFragmentManager, "test")
        }

        binding.activityMainCameraButton.setOnClick {
            startActivity(Intent(applicationContext, CameraActivity::class.java))
        }


    }
}

