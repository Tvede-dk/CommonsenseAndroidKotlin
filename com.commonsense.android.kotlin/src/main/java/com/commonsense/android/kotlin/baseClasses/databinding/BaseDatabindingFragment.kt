package com.commonsense.android.kotlin.baseClasses.databinding

import android.app.AlertDialog
import android.app.Dialog
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.extensions.dialogFillParentView
import com.commonsense.android.kotlin.android.extensions.getParrentContainerId
import com.commonsense.android.kotlin.baseClasses.BaseFragment
import com.commonsense.android.kotlin.baseClasses.pushNewFragmentTo
import com.commonsense.android.kotlin.baseClasses.replaceFragment

/**
 * created by Kasper Tvede on 29-09-2016.
 */
abstract class BaseDatabindingFragment<out T : ViewDataBinding> : BaseFragment() {

    val layoutInflator: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    var showDialogAsFullScreen = false


    abstract fun createView(inflater: LayoutInflater, parent: ViewGroup?): T

    val binding: T by lazy {
        createView(layoutInflator, parentView)
    }

    private var parentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        getParrentContainerId()?.let { activity.replaceFragment(it, otherFragment()) }
    }

    inline fun Fragment.pushThisFragment(otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { activity.pushNewFragmentTo(it, otherFragment()) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val res = builder
                .setCustomTitle(null)
                .setView(binding.root)
                .create()

        return res
    }

    override fun onResume() {
        if (showDialogAsFullScreen) {
            dialogFillParentView()
        }
        super.onResume()
    }


}