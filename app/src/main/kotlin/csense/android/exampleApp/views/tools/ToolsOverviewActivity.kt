package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.system.extensions.startActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import csense.android.exampleApp.databinding.ToolsOverviewBinding

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
        binding.toolsOverviewCrashlistener.setOnclickAsync {
            startActivity(ToolsCrashListenerActivity::class)
        }

        binding.toolsOverviewFps.setOnclickAsync {
            startActivity(ToolsFpsActivity::class)
        }

        binding.toolsOverviewPerformance.setOnclickAsync {
            startActivity(ToolsPerformanceActivity::class)
        }

    }

}