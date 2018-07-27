package csense.android.exampleApp.activity

import android.content.Intent
import com.commonsense.android.kotlin.system.base.BaseSplashActivity
import com.commonsense.android.kotlin.system.base.LayoutResList
import com.commonsense.android.kotlin.system.base.resListOf

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