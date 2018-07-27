package csense.android.exampleApp.activity

import com.commonsense.android.kotlin.system.extensions.*
import csense.android.exampleApp.fragments.*

/**
 * Created by Kasper Tvede on 24-06-2017.
 */

class Demo5Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SectionSwipeAdapterFragment())
    }
}



