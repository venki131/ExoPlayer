package com.example.exovideoplayer

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class WriteToFileActivity : AppCompatActivity() {

    private var openActivityBtn: Button? = null
    private var downloadHtmlBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_to_file)
        openActivityBtn = findViewById(R.id.open_activity)
        downloadHtmlBtn = findViewById(R.id.download_html)

        downloadHtmlBtn?.setOnClickListener {
            saveRemoteHtmlFile(this, Constants.privacyPolicyUrl, Constants.fileName) {}
        }

        openActivityBtn?.setOnClickListener {
            Intent(this, SecondActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun loadFileInWebView(webView: WebView?, file: File) {
        webView?.let {
            val baseUrl = "file://${file.parentFile?.absolutePath}/"
            val htmlContent = file.readText()
            it.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "utf-8", null)
        }
    }
    private fun loadLocalHtmlFile(webView: WebView, file: File) {
        val baseUrl = "file://${file.parentFile?.absolutePath}/"
        val htmlContent = file.readText()
        webView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "utf-8", null)
    }

    // Load HTML content using "file://" URL (API 21-29)
    private fun loadHtmlWithFileUrl(webView: WebView, file: File) {
        val htmlPath = "file://${file.absolutePath}"
        webView.loadUrl(htmlPath)
    }

    private fun writeToFile(
        content: String,
        fileName: String,
        mimeType: String,
        fileDirectory: File? = null,
        directory: String? = ""
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
                }

                val resolver = contentResolver
                val uri = resolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    values
                )
                uri?.let {
                    val outputStream = resolver.openOutputStream(uri)
                    outputStream?.use { stream ->
                        stream.write(content.toByteArray())
                    }
                    showToast("File written successfully")
                }
            } else {
                fileDirectory?.let { dir ->
                    if (!dir.exists()) {
                        dir.mkdirs() // Create the directory if it doesn't exist
                    }
                    val file = File(dir, fileName)
                    FileOutputStream(file).use { outputStream ->
                        outputStream.write(content.toByteArray())
                    }
                    showToast("File written successfully")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Failed to write file")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun readFile(
        fileName: String,
        fileDirectory: File? = null,
        directory: String? = ""
    ): String? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf(fileName)
                val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val cursor = contentResolver.query(
                    uri,
                    null,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex =
                            it.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                        val relativePath = it.getString(columnIndex)

                        val file = File(
                            Environment.getExternalStorageDirectory()
                                .absolutePath + File.separator + relativePath,
                            fileName
                        )

                        if (file.exists()) {
                            return file.readText()
                        }
                    }
                }
            } else {
                fileDirectory?.let { dir ->
                    val file = File(dir, fileName)
                    if (file.exists()) {
                        return file.readText()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun saveRemoteHtmlFile(
        context: Context,
        url: String,
        fileName: String,
        callback: (Boolean) -> Unit
    ) {
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                val fileToBeDeleted: File? = context.getFileStreamPath(fileName)
                fileToBeDeleted?.delete()
                val file = File(context.filesDir, fileName)
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        callback(false)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            callback(false)
                            return
                        }
                        val res = response.body
                        res?.let {
                            FileOutputStream(file).use { outputStream ->
                                outputStream.write(it.bytes())
                                callback(true)
                            }
                        }
                    }

                })
            } catch (e: Exception) {
                //FirebaseCrashlytics.getInstance().recordException(e) //TODO uncomment this
                e.printStackTrace()
            }
        }
    }
}