@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")


package com.commonsense.android.kotlin.views.databinding.activities

import android.os.*
import android.view.*
import androidx.databinding.*
import com.commonsense.android.kotlin.system.base.*

/**
 * created by Kasper Tvede
 * The basics of a databinding activity.
 *
 */

/**
 *
 */
typealias InflaterFunctionSimple<T> = (layoutInflater: LayoutInflater) -> T

/**
 * Base of a databinded activity
 * Wraps the databinding of an Activity
 * handles setup and the acccess to the binding
 * @param T : ViewDataBinding the view binding
 * @property binding T the view binding.
 */
abstract class BaseDatabindingActivity<out T : ViewDataBinding> : BaseActivity(), Databindable<T> {

    override val binding: T by lazy {
        createBinding().invoke(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.executePendingBindings()
        useBinding()
    }
}