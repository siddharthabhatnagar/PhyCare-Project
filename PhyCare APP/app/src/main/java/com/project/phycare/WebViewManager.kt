package com.project.phycare

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView

@SuppressLint("StaticFieldLeak")
object WebViewManager {
        var webView: WebView? = null

        fun init(context: Context, url: String) {
            if (webView == null) {
                webView = WebView(context.applicationContext).apply {
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            }
        }

        fun attachTo(container: ViewGroup) {
            webView?.parent?.let {
                (it as ViewGroup).removeView(webView)
            }
            container.addView(webView)
        }
    }
