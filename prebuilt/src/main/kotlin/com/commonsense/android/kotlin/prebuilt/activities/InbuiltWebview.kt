package com.commonsense.android.kotlin.prebuilt.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.views.extensions.loadUri

/**
 * Created by Kasper Tvede on 09-07-2017.
 */
open class InbuiltWebView : BaseActivity() {

    private val webView by lazy {
        WebView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent?.getParcelableExtra<Uri>(InbuiltWebView.uriIntentIndex)
        if (uri == null) {
            finish()
            return
        }
        setContentView(webView)
        webView.loadUri(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }

    //TODO add title, potential usage of CustomTabs (google project).
    //as well as JS... ect.
    // https://developer.chrome.com/multidevice/android/customtabs

    companion object {
        private val uriIntentIndex = "uri"
        /**
         *
         */
        fun showUri(uri: Uri, context: Context) {
            val intent = Intent(context, InbuiltWebView::class.java)
            intent.putExtra(uriIntentIndex, uri)
            context.startActivity(intent)
        }

    }
}