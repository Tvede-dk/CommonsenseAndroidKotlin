package com.commonsense.android.kotlin.databinding.activities

import android.content.res.Configuration
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem

/**
 * Created by Kasper Tvede on 01-11-2016.
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
        //TODO make this work again.
//        if (item?.itemId == R.id.home) {
//            onBackPressed()
//            return true
//        }

        return super.onOptionsItemSelected(item)
    }

}