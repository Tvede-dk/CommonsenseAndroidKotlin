package com.CommonSenseAndroidKotlin.example.activity

import android.content.Intent
import com.CommonSenseAndroidKotlin.example.databinding.CameraActivityBinding
import com.CommonSenseAndroidKotlin.example.fragments.CameraFragment
import com.commonsense.android.kotlin.system.base.replaceFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraActivity : BaseDatabindingActivity<CameraActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<CameraActivityBinding>
            = CameraActivityBinding::inflate


    override fun useBinding() {
        replaceFragment(binding.cameraActivityContent.id, CameraFragment())
//        Log.e("test,","aweqwe")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}