package com.example.destinationguide

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

class WebActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var webProgress: ProgressBar

    private lateinit var btnBack: Button
    private lateinit var btnForward: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web)

        // Initialize views

        webView = findViewById(R.id.webView)
        webProgress = findViewById(R.id.webProgress)

        btnBack = findViewById(R.id.btnBack)
        btnForward = findViewById(R.id.btnForward)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnHome = findViewById(R.id.btnHome)

        // Configure WebView

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Keep webpages inside the application

        webView.webViewClient = WebViewClient()

        // Loading progress

        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(
                view: WebView?,
                newProgress: Int
            ) {

                webProgress.progress = newProgress

                webProgress.visibility =
                    if (newProgress < 100) View.VISIBLE
                    else View.GONE
            }
        }

        // Get article URL

        val articleUrl = intent.getStringExtra("articleUrl")

        if (!articleUrl.isNullOrEmpty()) {
            webView.loadUrl(articleUrl)
        } else {
            Toast.makeText(
                this,
                "Article URL not available",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Back button

        btnBack.setOnClickListener {

            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }

        // Forward button

        btnForward.setOnClickListener {

            if (webView.canGoForward()) {
                webView.goForward()
            } else {
                Toast.makeText(
                    this,
                    "No forward page available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Refresh button

        btnRefresh.setOnClickListener {
            webView.reload()
        }

        // Home button

        btnHome.setOnClickListener {
            finish()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}