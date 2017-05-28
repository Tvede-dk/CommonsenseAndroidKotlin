package com.commonsense.android.kotlin.baseClasses

import android.support.annotation.IntRange
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

private data class FragmentWithTitle(val fragment: Fragment, val title: CharSequence)

public class BaseFragmentPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private var data = mutableListOf<FragmentWithTitle>()

    fun addFragment(fragment: Fragment, title: CharSequence) {
        data.add(FragmentWithTitle(fragment, title))
    }

    override fun getItem(@IntRange(from = 0) position: Int): Fragment = data[position].fragment

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence = data[position].title
}