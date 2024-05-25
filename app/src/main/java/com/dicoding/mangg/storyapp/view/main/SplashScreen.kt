package com.dicoding.mangg.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mangg.storyapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        binding.splashImage.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 6000)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.splashImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 9000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titletext =
            ObjectAnimator.ofFloat(binding.titleSplash, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(titletext)
            start()
        }
    }
}