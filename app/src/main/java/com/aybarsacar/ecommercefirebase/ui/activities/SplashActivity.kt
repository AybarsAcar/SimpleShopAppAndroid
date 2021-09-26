package com.aybarsacar.ecommercefirebase.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aybarsacar.ecommercefirebase.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

  private lateinit var _binding: ActivitySplashBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivitySplashBinding.inflate(layoutInflater)

    setContentView(_binding.root)

    // hide the status bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // make it full screen without the action bar at the top - hides it
      // for newer versions of Android
      window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
      @Suppress("DEPRECATION")
      window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
      )
    }

    Handler(Looper.getMainLooper()).postDelayed({
      startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
      finish()
    }, 1000)
  }
}