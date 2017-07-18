package com.CommonSenseAndroidKotlin.example.activity

import com.CommonSenseAndroidKotlin.example.databinding.TodoActivityBinding
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.commonsense.android.kotlin.baseClasses.databinding.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 14-07-2017.
 */
class TodoExampleActivity : BaseDatabindingActivity<TodoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<TodoActivityBinding> = TodoActivityBinding::inflate

    override fun useBinding() {
    }

}