package com.commonsense.android.kotlin.views.databinding.activities

import android.content.res.Configuration
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem

/**
 * Created by Kasper Tvede on 01-11-2016.
 */

/**
 * A Databinded activity with a toolbar
 * required methods delegate to this as well.
 *
 * @see BaseDatabindingActivity
 * @param out T : ViewDataBinding
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

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}