package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.base.time.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.extensions.*
import com.commonsense.android.kotlin.views.helpers.*
import csense.android.exampleApp.databinding.*
import csense.android.tools.anr.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*

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

        binding.toolsAnrTriggerButton.setOnclickAsyncSuspend {
            ANRWatcher.timeout.delay()
        }

        ANRWatcher.listener = {
            launchInUi("Anr watcher") {
                safeToast("ANR detected")
            }
        }

    }

}