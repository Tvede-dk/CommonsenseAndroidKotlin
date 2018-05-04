package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding

/**
 * Created by kasper on 25/07/2017.
 */

interface Databindable<out T : ViewDataBinding> {
    val binding: T
    fun useBinding()
    fun createBinding(): InflaterFunctionFull<T>
}
