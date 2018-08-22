package csense.android.exampleApp.views.widgets

import com.commonsense.android.kotlin.system.extensions.startActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import csense.android.exampleApp.databinding.WidgetsOverviewActivityBinding

class WidgetsOverviewActivity : BaseDatabindingActivity<WidgetsOverviewActivityBinding>() {

    override fun createBinding(): InflaterFunctionSimple<WidgetsOverviewActivityBinding> =
            WidgetsOverviewActivityBinding::inflate

    override fun useBinding() {
        binding.widgetsOverviewActivityRecyclerAdapterButton.setOnclickAsync {
            startActivity(WidgetsRecyclerExampleActivity::class)
        }
    }

}