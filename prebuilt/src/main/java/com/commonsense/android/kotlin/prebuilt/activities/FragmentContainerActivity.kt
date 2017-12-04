package com.commonsense.android.kotlin.prebuilt.activities

import android.databinding.ViewDataBinding
import android.support.v4.app.Fragment
import android.view.ViewGroup
import com.commonsense.android.kotlin.system.base.replaceFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity

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
