package csense.android.exampleApp.activity

import com.CommonSenseAndroidKotlin.example.activity.*
import csense.android.exampleApp.fragments.SearchAbleRecyclerDemo
import com.commonsense.android.kotlin.system.extensions.replaceFragment

/**
 * Created by kasper on 01/06/2017.
 */
class Demo2Activity : DemoActivity() {

    override fun useBinding() {
        replaceFragment(binding.demoActivityContainer.id, SearchAbleRecyclerDemo())
    }
}