package com.CommonSenseAndroidKotlin.example.activity

import android.content.Intent
import com.CommonSenseAndroidKotlin.example.databinding.CameraActivityBinding
import com.CommonSenseAndroidKotlin.example.fragments.CameraFragment
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.databinding.InflaterFunctionSimple
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraActivity : BaseDatabindingActivity<CameraActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<CameraActivityBinding>
            = CameraActivityBinding::inflate


    override fun useBinding() {
        replaceFragment(binding.cameraActivityContent.id, CameraFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}