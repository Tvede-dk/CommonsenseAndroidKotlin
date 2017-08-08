package com.CommonSenseAndroidKotlin.example.activity

import android.content.Intent
import android.os.Bundle
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.base.safeFinish

/**
 * Created by Kasper Tvede on 29-07-2017.
 */
class SplashExampleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        safeFinish()
    }
}