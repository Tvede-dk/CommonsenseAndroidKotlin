package com.commonsense.android.kotlin.prebuilt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import com.commonsense.android.kotlin.system.base.BaseActivity

/**
 * Created by Kasper Tvede on 09-07-2017.
 */
class InbuiltWebview : BaseActivity() {

    private val webView by lazy {
        WebView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent?.getStringExtra(InbuiltWebview.urlIntentIndex)
        if (url == null) {
            finish()
            return
        }
        setContentView(webView)
        webView.loadUrl(url)


    }

    companion object {
        val urlIntentIndex = "url"
        fun showUrl(url: String, context: Context) {
            val intent = Intent(context, InbuiltWebview::class.java)
            intent.putExtra(urlIntentIndex, url)
            context.startActivity(intent)
        }

    }
}