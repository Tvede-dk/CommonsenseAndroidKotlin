package csense.android.exampleApp.activity

import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import csense.android.exampleApp.databinding.*
import csense.android.exampleApp.fragments.*

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


open class DemoActivity : BaseDatabindingActivity<DemoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<DemoActivityBinding> = DemoActivityBinding::inflate

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SimpleRecyclerDemo())

    }
}