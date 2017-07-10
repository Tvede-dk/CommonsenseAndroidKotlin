package com.CommonSenseAndroidKotlin.example.activity

import android.content.Intent
import android.view.LayoutInflater
import com.CommonSenseAndroidKotlin.example.databinding.CameraActivityBinding
import com.CommonSenseAndroidKotlin.example.fragments.CameraFragment
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraActivity : BaseDatabindingActivity<CameraActivityBinding>() {

    override fun createBinding(inflater: LayoutInflater): CameraActivityBinding {
        return CameraActivityBinding.inflate(inflater)
    }

    override fun useBinding() {
        replaceFragment(binding.cameraActivityContent.id, CameraFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}