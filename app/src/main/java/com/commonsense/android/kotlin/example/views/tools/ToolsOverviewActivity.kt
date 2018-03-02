package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.example.databinding.ToolsOverviewBinding
import com.commonsense.android.kotlin.system.extensions.startActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsOverviewActivity : BaseDatabindingActivity<ToolsOverviewBinding>() {
    override fun createBinding(): InflaterFunctionSimple<ToolsOverviewBinding> =
            ToolsOverviewBinding::inflate

    override fun useBinding() {
        binding.toolsOverviewAnr.setOnclickAsync {
            startActivity(ToolsAnrActivity::class)
        }
    }

}