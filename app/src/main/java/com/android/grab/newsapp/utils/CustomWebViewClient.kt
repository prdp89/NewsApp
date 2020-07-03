package com.android.grab.newsapp.utils

import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import com.android.grab.newsapp.R

class CustomWebViewClient(
    private val progressBar: ProgressBar
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String?
    ): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        progressBar.visibility = View.VISIBLE
        view?.isEnabled = false
    }

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler,
        error: SslError?
    ) {
        // Ignore SSL certificate errors
        handler.proceed()
    }

    override fun onPageFinished(
        view: WebView?,
        url: String?
    ) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE
        view?.isEnabled = true
    }

    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        Toast.makeText(
            view?.context,
            view?.context?.getString(R.string.fail_news_url),
            Toast.LENGTH_SHORT
        ).show()
    }
}