package csense.android.exampleApp.views.tools

import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import csense.android.exampleApp.databinding.ToolsCrashBinding

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsCrashListenerActivity : BaseDatabindingActivity<ToolsCrashBinding>() {
    override fun createBinding(): InflaterFunctionSimple<ToolsCrashBinding> = ToolsCrashBinding::inflate

    override fun useBinding() {

    }

}