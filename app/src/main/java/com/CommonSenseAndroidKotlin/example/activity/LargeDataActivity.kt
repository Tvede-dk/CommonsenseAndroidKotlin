package com.CommonSenseAndroidKotlin.example.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.CommonSenseAndroidKotlin.example.R
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync

/**
 * Created by Kasper Tvede on 23-07-2017.
 */
data class LargeDataActivityData(val randomNumbers: List<Int>)


class LargeDataActivity : BaseActivityData<LargeDataActivityData>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.large_data_activity)
        safeToast("got items: ${data.randomNumbers.size}")
        findViewById<Button>(R.id.large_data_activity_main_click).setOnclickAsync(this::onMainActivityClicked)
        findViewById<Button>(R.id.large_data_activity_data_click).setOnclickAsync(this::ondataReload)
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

