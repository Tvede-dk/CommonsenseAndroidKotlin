@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.baseClasses

import android.annotation.*
import android.os.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.v4.app.*
import android.support.v4.view.*
import android.view.*
import com.commonsense.android.kotlin.base.extensions.collections.*

/**
 *
 * @property fragment Fragment
 * @property title CharSequence
 * @property position Int the positing "last set" via the pager adapter. this is mostly for internal stuff.
 * @constructor
 */
private data class FragmentWithTitle(val fragment: Fragment, val title: CharSequence, var lastPosition: Int)

/**
 * A simple non state handling (thus not the most efficient) PagerAdapter
 * it can take any fragment along a title, and will
 * @property data MutableList<FragmentWithTitle>
 * @property allFragments List<FragmentWithTitle>
 * @constructor
 */
class BaseFragmentPagerAdapter(val fragmentManager: FragmentManager) : PagerAdapter() {

    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    private var data = mutableListOf<FragmentWithTitle>()

    fun addFragment(fragment: Fragment, title: CharSequence = "") {
        data.add(FragmentWithTitle(fragment, title, data.size))
        notifyDataSetChanged()
    }


    override fun startUpdate(container: ViewGroup) {
        if (container.id == -1) {
            throw IllegalStateException("ViewPager with adapter $this requires a view id")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val transaction = getOrCreateTransaction()
        val itemId = this.getItemId(position)
        val name = makeFragmentName(container.id, itemId)
        var fragment = this.fragmentManager.findFragmentByTag(name)?.correctDialogFlag()
        if (fragment != null) {
            transaction.attach(fragment)
        } else {
            fragment = this.getItem(position).correctDialogFlag()
            transaction.show(fragment)
            transaction.add(container.id, fragment, makeFragmentName(container.id, itemId))
        }

        if (fragment !== this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }
        return fragment
    }

    private fun Fragment.correctDialogFlag(): Fragment = apply {
        if (this is DialogFragment) {
            this.showsDialog = false //make sure
        }
    }



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val transaction = getOrCreateTransaction()
        transaction.detach(`object` as Fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as? Fragment ?: return
        setPrimiaryItem(fragment)

    }

    @SuppressLint("CommitTransaction")//since the update finish update must be called by the viewpager
    private inline fun getOrCreateTransaction(): FragmentTransaction {
        val transaction = mCurTransaction ?: this.fragmentManager.beginTransaction()
        mCurTransaction = transaction
        return transaction
    }

    fun setPrimiaryItem(fragment: Fragment) {
        if (fragment === this.mCurrentPrimaryItem) {
            return
        }
        mCurrentPrimaryItem?.apply {
            setMenuVisibility(false)
            userVisibleHint = false
        }
        mCurrentPrimaryItem = fragment
        fragment.setMenuVisibility(true)
        fragment.userVisibleHint = true
    }

    override fun finishUpdate(container: ViewGroup) {
        mCurTransaction?.commitAllowingStateLoss()
        mCurTransaction = null
        fragmentManager.executePendingTransactions()

    }


    fun getItem(@IntRange(from = 0) position: Int): Fragment = data[position].fragment

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence = data[position].title

    fun removeFragment(fragment: Fragment) {
        data.removeAll {
            it.fragment === fragment
        }.ifTrue {
            //if we did remove some
            notifyDataSetChanged()
        }
    }


    fun getItemId(@IntRange(from = 0) position: Int): Long {
        //since the hashcode will default to the memory address of the object,
        // We will never have to come up with a uniq id for the object as the memory add is unique :)
        return getItem(position).hashCode().toLong()
    }

    // TODO: refactor into a datasstructure such that we can

    @PagerAdapterPosition
    override fun getItemPosition(`object`: Any): Int {
        //is not the right type ? skip it
        val fragment = `object` as? FragmentWithTitle
                ?: return PagerAdapter.POSITION_NONE

        //get what is possible from the index, if not safe then null is returned
        val atPosition = data.getSafe(fragment.lastPosition)
        val isRightPosition =
                atPosition?.fragment === fragment.fragment //are we that fragment /by address comparison
        if (!isRightPosition) {
            //update if different.
            fragment.lastPosition = data.indexOf(fragment)
        }
        //unchanged => not changed / the old index is the same still; if not then return "none" meaning "changed".
        return isRightPosition.map(PagerAdapter.POSITION_UNCHANGED, PagerAdapter.POSITION_NONE)
    }


    /**
     * Allows the user to perform "foreach" / filter, map ect over all fragments, while not modifying out list explicitly
     * they can however modify the fragments internally.
     * but that will always be an issue.
     */
    fun getAllFragments(): Sequence<Fragment> = data.asSequence().map { it.fragment }

    override fun saveState(): Parcelable? = null

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) = Unit

    private fun makeFragmentName(viewId: Int, id: Long): String {
        return "android:switcher:$viewId:$id"
    }
}


@IntDef(PagerAdapter.POSITION_UNCHANGED, PagerAdapter.POSITION_NONE)
@Retention(AnnotationRetention.SOURCE)
annotation class PagerAdapterPosition

