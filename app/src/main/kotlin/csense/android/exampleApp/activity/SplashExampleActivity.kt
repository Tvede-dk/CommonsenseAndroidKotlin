package csense.android.exampleApp.activity

import android.content.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.base.helpers.*

/**
 * Created by Kasper Tvede on 29-07-2017.
 */
class SplashExampleActivity : BaseSplashActivity() {
    override fun onAppLoaded() {
        startActivity(Intent(
                this,
                MainActivity::class.java))
    }

    override val viewsToPreload: LayoutResList = resListOf()
}