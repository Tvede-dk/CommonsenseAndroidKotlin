//@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
//
//package com.commonsense.android.kotlin.views.widgets.webview
//
//import android.content.*
//import android.net.*
//import android.os.Build.*
//import android.support.annotation.*
//import android.util.*
//import android.webkit.*
//import com.commonsense.android.kotlin.base.extensions.*
//import com.commonsense.android.kotlin.system.base.*
//import com.commonsense.android.kotlin.views.databinding.*
//import java.lang.ref.*
//
//
///**
// *
// */
//
//class BaseWebView(context: Context) : CustomDataBindingView<BaseWebviewViewBinding>(context) {
//
//    override fun inflate(): InflaterFunction<BaseWebviewViewBinding> =
//            BaseWebviewViewBinding::inflate
//
//    private val view: BaseWebViewImpl
//        get() = binding.baseWebviewViewWebview
//
//    private val internalChromeClient = BaseChromeWebViewClient()
//    private val internalWebClient = BaseWebViewClient()
//
//    fun allowFileUploads(act: BaseActivity) {
//        internalChromeClient.weakBaseActivity = act.weakReference()
//    }
//
//
//    override fun getStyleResource(): IntArray? = null
//
//    override fun updateView() {
//
//    }
//
//    //initialize all standard here
//    override fun afterSetupView() {
//        view.webChromeClient = internalChromeClient
//        view.webViewClient = internalWebClient
//    }
//
//
//    fun loadAsset() {
//
//    }
//
//    fun loadUrl() {
////        view.loadHtmlWithBaseURL()
//    }
//
//}
//
//internal class BaseWebViewImpl : WebView {
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//    @RequiresApi(VERSION_CODES.LOLLIPOP)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
//
//}
//
//private class BaseWebViewClient : WebViewClient()
//
//private class BaseChromeWebViewClient : WebChromeClient() {
//
//    var weakBaseActivity: WeakReference<BaseActivity>? = null
//
//    var allowUploadFiles: Boolean = true
//
//
//    // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
//    fun openFileChooser(uploadMsg: ValueCallback<Uri>,
//                        acceptType: String, capture: String) {
//
//    }
//
//    override fun onShowFileChooser(webView: WebView?,
//                                   filePathCallback: ValueCallback<Array<Uri>>?,
//                                   fileChooserParams: FileChooserParams?): Boolean {
//        return true
//    }
//
//}