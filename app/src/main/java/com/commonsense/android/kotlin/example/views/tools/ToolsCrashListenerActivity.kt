package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.example.databinding.ToolsCrashBinding
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsCrashListenerActivity : BaseDatabindingActivity<ToolsCrashBinding>() {
    override fun createBinding(): InflaterFunctionFull<ToolsCrashBinding> = ToolsCrashBinding::inflate

    override fun useBinding() {

    }

}