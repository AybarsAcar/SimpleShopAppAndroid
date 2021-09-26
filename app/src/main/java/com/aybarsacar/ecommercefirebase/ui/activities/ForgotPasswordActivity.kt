package com.aybarsacar.ecommercefirebase.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : BaseActivity() {

  private lateinit var _binding: ActivityForgotPasswordBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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

    setupActionBar()

    _binding.btnSubmit.setOnClickListener {
      val email = _binding.etEmailForgotPassword.text.toString().trim { it <= ' ' }

      if (email.isEmpty()) {
        displaySnackBar("Please enter your email address", true)
      } else {
        handleResetPassword(email)
      }
    }
  }


  private fun handleResetPassword(email: String) {

    displayLoadingProgressDialog()

    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
      .addOnCompleteListener { task ->

        hideLoadingProgressDialog()

        if (task.isSuccessful) {
          // send the link
          displaySnackBar("Email has sent successfully", false)

        } else {

          displaySnackBar(task.exception!!.message.toString(), true)

        }
      }
  }


  private fun setupActionBar() {

    setSupportActionBar(_binding.toolbarForgotPassword)

    val actionBar = supportActionBar

    // change the colour of our vector asset on runtime
    val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_ios_new_24)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.white))


    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(wrappedDrawable)
    }

    _binding.toolbarForgotPassword.setNavigationOnClickListener { onBackPressed() }
  }
}