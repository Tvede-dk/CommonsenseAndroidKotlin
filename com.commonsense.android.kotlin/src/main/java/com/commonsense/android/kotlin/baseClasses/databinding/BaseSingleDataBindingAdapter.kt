package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.baseClasses.BaseAdapter

/**
 * created by Kasper Tvede on 29-09-2016.
 */

abstract class BaseSingleDataBindingAdapter<T, VB : ViewDataBinding>(context: Context, var viewbindingClass: Class<VB>, @LayoutRes var layoutRes: Int) : BaseAdapter<T>(context) {


    abstract fun populateView(item: T, binding: VB)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item: T = getItem(position) ?: throw RuntimeException()
        val binding: VB
        if (convertView != null && convertView.tag?.let { it::class.java } == viewbindingClass) {
            binding = viewbindingClass.cast(convertView.tag)
        } else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, parent, false)
            binding.root.tag = binding
        }
        populateView(item, binding)
        return binding.root
    }
}