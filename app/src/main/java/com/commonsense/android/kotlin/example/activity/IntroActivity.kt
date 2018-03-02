package com.commonsense.android.kotlin.example.activity

import com.commonsense.android.kotlin.example.databinding.IntroActivityBinding
import com.commonsense.android.kotlin.system.extensions.startActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class IntroActivity : BaseDatabindingActivity<IntroActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<IntroActivityBinding> = IntroActivityBinding::inflate

    override fun useBinding() {
        binding.introActivityProceed.setOnclickAsync { startActivity(MainActivity::class) }
    }


}