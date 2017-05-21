package com.commonsense.android.kotlin.baseClasses.databinding

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.extensions.getParrentContainerId
import com.commonsense.android.kotlin.baseClasses.BaseFragment
import com.commonsense.android.kotlin.baseClasses.pushNewFragmentTo
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * Created by admin on 29-09-2016.
 */
abstract class BaseDatabindingFragment<out T : ViewDataBinding> : BaseFragment() {

    val layoutInflator: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    abstract fun createView(inflater: LayoutInflater, parent: ViewGroup?): T

    val binding: T by lazy {
        createView(layoutInflator, parentView)
    }

    private var parentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parentView = container
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        useBinding()
    }

    abstract fun useBinding()

    inline fun replaceThisFragment(otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { activity.replaceFragment(it, otherFragment()) }
    }

    inline fun Fragment.pushThisFragment(otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { activity.pushNewFragmentTo(it, otherFragment()) }
    }

}