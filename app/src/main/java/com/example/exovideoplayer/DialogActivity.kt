package com.example.exovideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        DialogFragmentTest().show(supportFragmentManager, "Test Fragment")
    }
}