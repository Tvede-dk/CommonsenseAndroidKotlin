package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.views.baseClasses.BaseAdapter

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