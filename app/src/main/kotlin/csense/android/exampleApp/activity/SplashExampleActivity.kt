package csense.android.exampleApp.activity

import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.extensions.*

/**
 * Created by Kasper Tvede on 29-07-2017.
 */
class SplashExampleActivity : BaseSplashActivity() {
    override fun onAppLoaded() {
        startActivity(MainActivity::class)
    }

    override val viewsToPreload: LayoutResList = resListOf()
}