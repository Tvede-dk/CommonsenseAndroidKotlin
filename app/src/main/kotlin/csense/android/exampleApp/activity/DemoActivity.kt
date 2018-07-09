package csense.android.exampleApp.activity

import com.CommonSenseAndroidKotlin.example.databinding.DemoActivityBinding
import csense.android.exampleApp.fragments.SimpleRecyclerDemo
import com.commonsense.android.kotlin.system.extensions.replaceFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


open class DemoActivity : BaseDatabindingActivity<DemoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<DemoActivityBinding> = DemoActivityBinding::inflate

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SimpleRecyclerDemo())

    }
}