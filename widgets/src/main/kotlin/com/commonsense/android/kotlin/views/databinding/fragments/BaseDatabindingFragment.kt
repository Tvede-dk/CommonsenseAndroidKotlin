@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.databinding.fragments

import android.app.*
import android.databinding.*
import android.os.*
import android.support.v4.app.Fragment
import android.view.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.*

typealias InflateBinding<T> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> T

/**
 * created by Kasper Tvede on 29-09-2016.
 */
abstract class BaseDatabindingFragment<out T : ViewDataBinding> : BaseFragment() {

    var showDialogAsFullScreen = false

    abstract fun getInflater(): InflateBinding<T>

    private val ourLayoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
    private val inflationFunction by lazy {
        getInflater()
    }

    val binding: T by lazy {
        inflationFunction(ourLayoutInflater, parentView, false)
    }

    private var parentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (showsDialog) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
        parentView = container
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }

    abstract fun useBinding()

    inline fun replaceThisFragment(otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { fragmentManager?.replaceFragment(it, otherFragment()) }
    }

    //adds a new fragment after the current fragment
    inline fun Fragment.pushThisFragment(crossinline otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { fragmentManager?.pushNewFragmentTo(it, otherFragment()) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context, R.style.TransperantDialog)
        return builder
                .setCustomTitle(null)
                .setView(binding.root)
                .create()
    }

    override fun onResume() {
        if (showDialogAsFullScreen) {
            dialogFillParentView()
        }
        super.onResume()
    }


}