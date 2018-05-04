package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.example.databinding.ToolsPerformanceBinding
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsPerformanceActivity : BaseDatabindingActivity<ToolsPerformanceBinding>() {

    override fun createBinding(): InflaterFunctionFull<ToolsPerformanceBinding> =
            ToolsPerformanceBinding::inflate

    override fun useBinding() {
    }
}