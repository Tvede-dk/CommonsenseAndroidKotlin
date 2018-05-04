package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.base.time.delay
import com.commonsense.android.kotlin.example.databinding.ToolsAnrBinding
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.tools.anr.ANRWatcher
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import com.commonsense.android.kotlin.views.helpers.OnTextChangedWatcher
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */

class ToolsAnrActivity : BaseDatabindingActivity<ToolsAnrBinding>() {

    override fun createBinding(): InflaterFunctionFull<ToolsAnrBinding> = ToolsAnrBinding::inflate

    override fun useBinding() {
        binding.toolsAnrEnableListener.setOnCheckedChangeListener { _, isChecked ->
            ANRWatcher.enabled = isChecked
        }
        binding.toolsAnrTimeoutField.addTextChangedListener(OnTextChangedWatcher {
            ANRWatcher.timeout = TimeUnit.Milliseconds(it.toString().toLongOrNull() ?: 5000)
        })

        binding.toolsAnrTriggerButton.setOnclickAsync {
            runBlocking {
                ANRWatcher.timeout.delay()
            }
        }

        ANRWatcher.listener = {
            launchInUi("Anr watcher") {
                safeToast("ANR detected")
            }
        }

    }

}