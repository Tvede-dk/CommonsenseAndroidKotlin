@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import androidx.viewpager.widget.*


/**
 * Sets the adapter and registers the onpage changed listener
 * to make this super simple use the BaseViewPagerOnChangeListener
 * @receiver ViewPager
 * @param newAdapter PagerAdapter
 * @param onPageChangeListener ViewPager.OnPageChangeListener
 */
inline fun ViewPager.setAdapterAndListener(newAdapter: PagerAdapter, onPageChangeListener: ViewPager.OnPageChangeListener) {
    adapter = newAdapter
    addOnPageChangeListener(onPageChangeListener)
}