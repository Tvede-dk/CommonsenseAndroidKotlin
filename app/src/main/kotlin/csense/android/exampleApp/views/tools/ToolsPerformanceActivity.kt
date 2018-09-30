package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.views.databinding.activities.*
import csense.android.exampleApp.databinding.*

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