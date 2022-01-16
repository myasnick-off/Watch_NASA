package com.example.watchnasa.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.watchnasa.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val handler: Handler by lazy {
        Handler(mainLooper)
    }

    private var _binding: ActivitySplashBinding? = null
    private val binding: ActivitySplashBinding
    get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashImageView.animate()
            .rotationBy(360F)
            .alphaBy(1.0F)
            .duration = 2200

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
//            finish()
        }, 2500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}