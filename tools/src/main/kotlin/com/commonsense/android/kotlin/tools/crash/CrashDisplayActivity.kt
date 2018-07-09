package com.commonsense.android.kotlin.tools.crash

import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.tools.databinding.CrashActivityViewBinding
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivityWithData
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull
import kotlin.reflect.KClass

/**
 * Created by Kasper Tvede on 01-02-2018.
 * Purpose:
 *
 */
data class CrashDisplayData(val thread: Thread?, val throwable: Throwable?)


class CrashDisplayActivity : BaseDatabindingActivityWithData<CrashActivityViewBinding, CrashDisplayData>() {
    override fun createBinding(): InflaterFunctionFull<CrashActivityViewBinding> = CrashActivityViewBinding::inflate

    override val dataClass: KClass<CrashDisplayData>
        get() = CrashDisplayData::class

    override fun useBinding() {
        safeToast("hello!!")
    }

}