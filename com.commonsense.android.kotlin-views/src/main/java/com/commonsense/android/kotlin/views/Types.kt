package com.commonsense.android.kotlin.views

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by Kasper Tvede on 22-07-2017.
 */
typealias ViewInflatingFunction<Vm> = (inflater: LayoutInflater,
                                       parent: ViewGroup?,
                                       attach: Boolean) -> Vm


