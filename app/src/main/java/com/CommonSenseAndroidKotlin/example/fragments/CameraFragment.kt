package com.CommonSenseAndroidKotlin.example.fragments

import com.CommonSenseAndroidKotlin.example.databinding.CameraFragmentDemoBinding
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.android.image.PictureRetriver
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingFragment
import com.commonsense.android.kotlin.baseClasses.databinding.InflateBinding

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraFragment : BaseDatabindingFragment<CameraFragmentDemoBinding>() {
    override fun getInflater(): InflateBinding<CameraFragmentDemoBinding>
            = CameraFragmentDemoBinding::inflate

    private val imageHelper by lazy {
        PictureRetriver(activity as BaseActivity)
    }

    override fun useBinding() {
        binding.cameraFragmentImagesTake.setOnclickAsync {
            imageHelper.getImage()
        }
    }

}