package com.commonsense.android.kotlin.example.views.dataActivity

import com.commonsense.android.kotlin.system.base.helpers.BaseActivityData

/**
 * Created by Kasper Tvede on 09-02-2018.
 * Purpose:
 *
 */

data class SecondActivityData(val someOtherNumber: Int, val someText: String)

class SecondActivityWithData() : BaseActivityData<SecondActivityData>() {

}