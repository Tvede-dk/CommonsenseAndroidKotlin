package com.CommonSenseAndroidKotlin.example.activity


import android.content.Intent
import com.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.CommonSenseAndroidKotlin.example.fragments.EditDatabindingFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnClick
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync


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

