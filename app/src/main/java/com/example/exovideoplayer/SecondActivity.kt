package com.example.exovideoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import java.io.File

class SecondActivity : AppCompatActivity() {

    private var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        webView = findViewById(R.id.webview)

        val file = File(filesDir, Constants.fileName)

        if (file.exists()) {
            runOnUiThread {
                loadFileInWebView(webView, file)
            }
        } else {
            webView?.loadUrl(Constants.assetUrl)
        }
    }

    private fun loadFileInWebView(webView: WebView?, file: File) {
        webView?.let {
            val baseUrl = "file://${file.parentFile?.absolutePath}/"
            val htmlContent = file.readText()
            it.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "utf-8", null)
        }
    }
}