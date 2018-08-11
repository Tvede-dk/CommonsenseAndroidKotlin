package com.commonsense.android.kotlin.views.widgets.webview

import android.content.*
import android.os.Build.*
import android.support.annotation.*
import android.util.*
import android.webkit.*
import com.commonsense.android.kotlin.views.databinding.*

/**
 *
 */

class BaseWebView(context: Context) : CustomDataBindingView<BaseWebviewViewBinding>(context) {

    override fun inflate(): InflaterFunction<BaseWebviewViewBinding> =
            BaseWebviewViewBinding::inflate

    private val view: BaseWebViewImpl
        get() = binding.baseWebviewViewWebview

    private val internalChromeClient = BaseChromeWebViewClient()
    private val internalWebClient = BaseWebViewClient()


    var allowedUploadFiles
        get() = internalChromeClient.allowUploadFiles
        set(newValue) {
            internalChromeClient.allowUploadFiles = newValue
        }

    override fun getStyleResource(): IntArray? = null

    override fun updateView() {

    }

    //initialize all standard here
    override fun afterSetupView() {
        view.webChromeClient = internalChromeClient
        view.webViewClient = internalWebClient
    }


    fun loadAsset() {

    }

    fun loadUrl() {
//        view.loadHtmlWithBaseURL()
    }

}

internal class BaseWebViewImpl : WebView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

}

private class BaseWebViewClient : WebViewClient() {

}

private class BaseChromeWebViewClient : WebChromeClient() {
    var allowUploadFiles: Boolean = true

}