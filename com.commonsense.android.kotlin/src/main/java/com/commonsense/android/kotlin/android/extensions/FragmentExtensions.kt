package com.commonsense.android.kotlin.android.extensions

import android.support.v4.app.Fragment
import android.view.ViewGroup

/**
 * Created by Kasper Tvede on 10-01-2017.
 */


fun Fragment.getParrentContainerId() = (view?.parent as? ViewGroup)?.id

