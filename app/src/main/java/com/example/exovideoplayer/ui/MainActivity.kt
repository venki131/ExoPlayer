package com.example.exovideoplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exovideoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.customText.addIgnoredView(viewBinding.text1)
        viewBinding.customText.addIgnoredView(viewBinding.googleImg)
        //viewBinding.customText.removeIgnoredView(viewBinding.googleImg)
        viewBinding.customText.disabled = true
    }
}