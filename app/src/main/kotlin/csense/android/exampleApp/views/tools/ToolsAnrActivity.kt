package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.base.time.delay

import com.commonsense.android.kotlin.system.extensions.safeToast

import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import com.commonsense.android.kotlin.views.helpers.OnTextChangedWatcher
import csense.android.exampleApp.databinding.ToolsAnrBinding
import csense.android.tools.anr.ANRWatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */

class ToolsAnrActivity : BaseDatabindingActivity<ToolsAnrBinding>() {

    override fun createBinding(): InflaterFunctionSimple<ToolsAnrBinding> = ToolsAnrBinding::inflate

    override fun useBinding() {
        ANRWatcher.logTimings = true
        binding.toolsAnrEnableListener.setOnCheckedChangeListener { _, isChecked ->
            ANRWatcher.enabled = isChecked
        }
        binding.toolsAnrTimeoutField.addTextChangedListener(OnTextChangedWatcher {
            ANRWatcher.timeout = TimeUnit.MilliSeconds(it.toString().toLongOrNull() ?: 5000)
        })

        binding.toolsAnrTriggerButton.setOnclickAsync {
            async(UI) {
                runBlocking {
                    ANRWatcher.timeout.delay()
                }
            }
        }

        ANRWatcher.listener = {
            launchInUi("Anr watcher") {
                safeToast("ANR detected")
            }
        }

    }

}