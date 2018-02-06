package com.commonsense.android.kotlin.tools.crash

import com.commonsense.android.kotlin.tools.databinding.CrashActivityViewBinding
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivityWithData
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 01-02-2018.
 * Purpose:
 *
 */
data class CrashDisplayData(val thread: Thread?, val throwable: Throwable?)

class CrashDisplayActivity : BaseDatabindingActivityWithData<CrashActivityViewBinding, CrashDisplayData>() {
    override fun createBinding(): InflaterFunctionSimple<CrashActivityViewBinding> = CrashActivityViewBinding::inflate

    override fun useBinding() {

    }

    val adapter by lazy {

    }
}