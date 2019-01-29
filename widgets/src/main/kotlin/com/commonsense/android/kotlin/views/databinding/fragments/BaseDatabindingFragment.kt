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
 * created by Kasper Tvede
 */
abstract class BaseDatabindingFragment<out T : ViewDataBinding> : BaseFragment() {

    /**
     * If true, will force the dialog to take up the whole screen,
     * and on newer devices also hide the status bar.
     */
    var showDialogAsFullScreen = false

    abstract fun getInflater(): InflateBinding<T>

    private val ourLayoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private val inflationFunction by lazy {
        getInflater()
    }
    /**
     * The ViewBinding.
     */
    val binding: T by lazy {
        inflationFunction(ourLayoutInflater, parentView, false)
    }
    /**
     * Only used when we are embedded and needs to respect the given sizing that the xml
     * provides
     */
    private var parentView: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //if its not a dialog, we are to use the container to do the inflation correctly.
        if (!showsDialog) {
            parentView = container
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.executePendingBindings()
        useBinding()
    }

    /**
     * Use binding to setup and update the view;
     * This will be called when the view is ready and all is set up.
     */
    abstract fun useBinding()

    /**
     * Replaces this fragment with the given fragment.
     * @param otherFragment () -> Fragment
     */
    inline fun replaceThisFragment(otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { fragmentManager?.replaceFragment(it, otherFragment()) }
    }

    /**
     * adds a new fragment after the current fragment
     * @receiver Fragment
     * @param otherFragment () -> Fragment
     */
    inline fun Fragment.pushThisFragment(crossinline otherFragment: () -> Fragment) {
        getParrentContainerId()?.let { fragmentManager?.pushNewFragmentTo(it, otherFragment()) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        //DO ONLY USE DIALOG; AS IT DOES NOT MODIFY THE STYLING / WAY THE WINDOW WORK.
        return Dialog(context, R.style.TransperantDialog)
    }

    override fun onResume() {
        if (showsDialog && showDialogAsFullScreen) {
            dialogFillParentView()
        }
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { updateWindowBeforeShowing(it) }
    }

    /**
     * Hookpoint before the fragment gets to far in its lifecycle, to safely modify the window.
     * @param window Window
     * this is called after onStart, since modifying the window requires it to "be there", but before the data is displayed
     * see various only diagrams over the lifecycle to have an idea.
     */
    open fun updateWindowBeforeShowing(window: Window) {

    }
}
