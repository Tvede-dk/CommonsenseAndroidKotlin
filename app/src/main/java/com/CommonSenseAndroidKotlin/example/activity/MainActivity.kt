package com.CommonSenseAndroidKotlin.example.activity


import android.content.Intent
import android.view.LayoutInflater
import com.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.commonsense.android.kotlin.android.extensions.safeToast
import com.commonsense.android.kotlin.android.extensions.widets.setOnClick
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import kotlinx.coroutines.experimental.delay


class MainActivity : BaseDatabindingActivity<ActivityMainBinding>() {

    var counter = 0

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

        binding.activityMainAdvanceAsyncButton.setOnclickAsync {
            safeToast("async clicked ${counter++}")
            delay(2000)
        }

    }

    override fun createBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)


}

