package com.commonsense.android.kotlin.views.databinding.activities

import android.content.res.*
import android.os.*
import android.view.*
import androidx.appcompat.app.*
import androidx.databinding.*

/**
 * Created by Kasper Tvede on 01-11-2016.
 */

/**
 * A Databinded activity with a toolbar
 * required methods delegate to this as well.
 *
 * @see BaseDatabindingActivity
 * @param T : ViewDataBinding
 * @property drawerToggle ActionBarDrawerToggle the toolbar
 *
 */
abstract class BaseDatabindingActivityToolbar<out T : ViewDataBinding> : BaseDatabindingActivity<T>() {


    abstract val drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerToggle.isDrawerIndicatorEnabled = true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}