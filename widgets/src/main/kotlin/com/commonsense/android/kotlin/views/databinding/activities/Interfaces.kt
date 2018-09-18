package com.commonsense.android.kotlin.views.databinding.activities

import android.databinding.ViewDataBinding

/**
 * Created by kasper on 25/07/2017.
 */
/**
 * Describes what is required to be data bindable, akk use a view data binding
 * @param out T : ViewDataBinding the type of view binding
 * @property binding T the binding (access here to)
 */
interface Databindable<out T : ViewDataBinding> {
    val binding: T
    /**
     * callback/ hookpoint for using the binding
     */
    fun useBinding()

    /**
     * Retriever for getting a way to create this viewBinding.
     * @return InflaterFunctionSimple<T>
     */
    fun createBinding(): InflaterFunctionSimple<T>
}
