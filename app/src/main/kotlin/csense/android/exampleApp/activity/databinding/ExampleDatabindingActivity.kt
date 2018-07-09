package csense.android.exampleApp.activity.databinding

import com.CommonSenseAndroidKotlin.example.databinding.*
import com.commonsense.android.kotlin.views.databinding.activities.*

/**
 * Example's usage
 */
class ExampleDatabindingActivity : BaseDatabindingActivity<ExampleActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<ExampleActivityBinding> =
            ExampleActivityBinding::inflate

    override fun useBinding() {
        //use binding, which is not null anymore;
        binding.exampleActivityTextview.text = "example"
    }
}