package com.commonsense.android.kotlin.prebuilt.activities

import android.databinding.*
import android.support.v4.app.*
import android.view.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*

/**
 * Created by Kasper Tvede on 26-08-2017.
 *
 */
abstract class FragmentContainerActivity<ViewBinding : ViewDataBinding, FragmentType : Fragment>
    : BaseDatabindingActivity<ViewBinding>() {

    val fragment by lazy {
        createFragment()
    }

    abstract fun createFragment(): FragmentType

    abstract fun afterFragmentAttached()

    abstract fun getFragmentContainer(): ViewGroup


    override fun useBinding() {
        replaceFragment(getFragmentContainer().id, fragment)
        afterFragmentAttached()
    }
}
