package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import csense.android.exampleApp.databinding.ToolsPerformanceBinding

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsPerformanceActivity : BaseDatabindingActivity<ToolsPerformanceBinding>() {

    override fun createBinding(): InflaterFunctionSimple<ToolsPerformanceBinding> =
            ToolsPerformanceBinding::inflate

    override fun useBinding() {
    }
}