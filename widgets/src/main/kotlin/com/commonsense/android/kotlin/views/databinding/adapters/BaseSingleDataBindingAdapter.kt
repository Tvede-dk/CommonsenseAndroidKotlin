@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.*
import android.databinding.*
import android.support.annotation.*
import android.view.*
import com.commonsense.android.kotlin.views.baseClasses.*

/**
 * created by Kasper Tvede on 29-09-2016.
 */

abstract class BaseSingleDataBindingAdapter<T, VB : ViewDataBinding>(
        context: Context,
        val viewbindingClass: Class<VB>,
        @LayoutRes val layoutRes: Int) : BaseAdapter<T>(context) {

    abstract fun populateView(item: T, binding: VB)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item: T = getItem(position) ?: throw RuntimeException()
        val binding: VB
        if (convertView != null && convertView.tag?.let { it::class.java } == viewbindingClass) {
            binding = viewbindingClass.cast(convertView.tag) ?: throw RuntimeException(
                    "we found an equally view binding class," +
                            " but casting caused a null, thus the element is bad / wrong.")
        } else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, parent, false)
            binding.root.tag = binding
        }
        populateView(item, binding)
        return binding.root
    }
}