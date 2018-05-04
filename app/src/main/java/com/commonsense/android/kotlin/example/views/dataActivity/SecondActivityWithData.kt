package com.commonsense.android.kotlin.example.views.dataActivity

import com.commonsense.android.kotlin.example.R
import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.system.logging.logError
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

/**
 * Created by Kasper Tvede on 09-02-2018.
 * Purpose:
 *
 */
@Serializable
data class SecondActivityData(val someOtherNumber: Int, val someText: String)

class SecondActivityWithData : BaseActivityData<SecondActivityData>() {

    override val dataClass: KClass<SecondActivityData>
        get() = SecondActivityData::class

    override fun onDataReady() {
        setContentView(R.layout.start_activity_with_data_second)
        async {
            delay(5000)
            safeToast(data.someText)
        }
    }

    override fun serializeData(input: SecondActivityData): String {
        return ""
    }

    override fun deserializeData(serializedData: String): SecondActivityData? {
        logError("deserializing child")
        return SecondActivityData(99,"im deseralized")
    }
}