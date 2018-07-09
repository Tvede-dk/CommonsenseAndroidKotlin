package csense.android.exampleApp.activity

import android.content.Intent
import android.widget.Button
import com.CommonSenseAndroidKotlin.example.R
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.views.extensions.setOnclickAsyncEmpty

/**
 * Created by Kasper Tvede on 23-07-2017.
 */
data class LargeDataActivityData(val randomNumbers: List<Int>)


class LargeDataActivity : BaseActivityData<LargeDataActivityData>() {


    override fun onSafeData() {
        setContentView(R.layout.large_data_activity)
        safeToast("got items: ${data.randomNumbers.size}")
        findViewById<Button>(R.id.large_data_activity_main_click).setOnclickAsyncEmpty(this::onMainActivityClicked)
        findViewById<Button>(R.id.large_data_activity_data_click).setOnclickAsyncEmpty(this::ondataReload)
    }

    private fun ondataReload() {
        safeToast("got items: ${data.randomNumbers.size}")
    }

    fun onMainActivityClicked() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun beforeCloseOnBadData() {
        super.beforeCloseOnBadData()
        safeToast("Close before bad data !??!?")
    }

    companion object {
        fun generateExtremeLargeData(sizeInMB: Int): LargeDataActivityData {
            val count = (sizeInMB * 1024 * 1024) / 16
            return LargeDataActivityData((0 until count).map { it })
        }
    }
}



