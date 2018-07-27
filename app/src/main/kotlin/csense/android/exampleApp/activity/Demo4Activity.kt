package csense.android.exampleApp.activity

import com.commonsense.android.kotlin.system.extensions.*
import csense.android.exampleApp.fragments.*

/**
 * Created by kasper on 01/06/2017.
 */
class Demo4Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, EditDatabindingFragment())
    }
}