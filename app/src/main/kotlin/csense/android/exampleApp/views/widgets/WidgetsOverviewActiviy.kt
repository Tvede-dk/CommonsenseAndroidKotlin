package csense.android.exampleApp.views.widgets

import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*
import csense.android.exampleApp.views.widgets.pagerAdapter.*

class WidgetsOverviewActivity : BaseDatabindingActivity<WidgetsOverviewActivityBinding>() {

    override fun createBinding(): InflaterFunctionSimple<WidgetsOverviewActivityBinding> =
            WidgetsOverviewActivityBinding::inflate

    override fun useBinding() {
        binding.widgetsOverviewActivityRecyclerAdapterButton.setOnclickAsync {
            startActivity(WidgetsRecyclerExampleActivity::class)
        }
        binding.widgetsOverviewActivityFragmentPagerButton.setOnclickAsync {
            startActivity(WidgetsBaseFragmentPagerAdapterActivity::class)
        }

    }

}