package com.commonsense.android.kotlin.example.views.dataActivity

import android.os.Bundle
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.base.helpers.startActivityWithData

/**
 * Created by Kasper Tvede on 09-02-2018.
 * Purpose:
 *
 */


class StartActivityData : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityWithData(SecondActivityWithData::class,
                SecondActivityData(42, "magic is this, like it I do"))
        finish()
    }

}