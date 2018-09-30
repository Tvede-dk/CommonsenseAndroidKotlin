package csense.android.exampleApp.views.widgets

import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*

class WidgetsOverviewActivity : BaseDatabindingActivity<WidgetsOverviewActivityBinding>() {

    override fun createBinding(): InflaterFunctionSimple<WidgetsOverviewActivityBinding> =
            WidgetsOverviewActivityBinding::inflate

    override fun useBinding() {
        binding.widgetsOverviewActivityRecyclerAdapterButton.setOnclickAsync {
            startActivity(WidgetsRecyclerExampleActivity::class)
        }
    }

}