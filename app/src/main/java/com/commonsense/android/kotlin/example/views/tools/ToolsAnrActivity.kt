package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.example.databinding.ToolsAnrBinding
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.tools.TimeUnit
import com.commonsense.android.kotlin.tools.anr.ANRWatcher
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import com.commonsense.android.kotlin.views.helpers.OnTextChangedWatcher

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */

class ToolsAnrActivity : BaseDatabindingActivity<ToolsAnrBinding>() {

    override fun createBinding(): InflaterFunctionSimple<ToolsAnrBinding> = ToolsAnrBinding::inflate

    override fun useBinding() {
        binding.toolsAnrEnableListener.setOnCheckedChangeListener { buttonView, isChecked ->
            ANRWatcher.enabled = isChecked
        }
        binding.toolsAnrTimeoutField.addTextChangedListener(OnTextChangedWatcher {
            ANRWatcher.timeout = TimeUnit.Milliseconds(it.toString().toLongOrNull() ?: 5000)
        })

        binding.toolsAnrTriggerButton.setOnclickAsync {
            Thread.sleep(ANRWatcher.timeout.toMilliseconds().getMilliseconds() + 5000)
        }

        ANRWatcher.listener = {
            launchInUi("") {
                safeToast("ANR detected")
            }
        }

    }

}