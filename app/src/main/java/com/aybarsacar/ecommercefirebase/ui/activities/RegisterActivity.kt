package com.aybarsacar.ecommercefirebase.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityRegisterBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class RegisterActivity : BaseActivity() {

  private lateinit var _binding: ActivityRegisterBinding
  private lateinit var _fireStoreService: FirestoreService


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // inject dependencies
    _binding = ActivityRegisterBinding.inflate(layoutInflater)
    _fireStoreService = FirestoreService()

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

    _binding.tvLogin.setOnClickListener {
      onBackPressed()
    }

    _binding.btnRegister.setOnClickListener {
      handleRegisterUser()
    }
  }


  private fun setupActionBar() {

    setSupportActionBar(_binding.toolbarRegisterActivity)

    // change the colour of our vector asset on runtime
    val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_ios_new_24)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.primary_dark))

    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    _binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
  }


  private fun validateRegisterDetails(): Boolean {

    return when {

      TextUtils.isEmpty(_binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("First name is required", true)
        false
      }

      TextUtils.isEmpty(_binding.etLastName.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Last name is required", true)
        false
      }

      TextUtils.isEmpty(_binding.etEmail.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Email is required", true)
        false
      }

      TextUtils.isEmpty(_binding.etPassword.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Password is required", true)
        false
      }

      TextUtils.isEmpty(_binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Password confirmation is required", true)
        false
      }

      _binding.etPassword.text.toString().trim { it <= ' ' } != _binding.etConfirmPassword.text.toString()
        .trim { it <= ' ' } -> {
        displaySnackBar("Passwords do not match", true)
        false
      }

      !_binding.cbTermsAndCondition.isChecked -> {
        displaySnackBar("Please agree to the terms and conditions before registering", true)
        false
      }

      // no form validation error
      else -> true
    }
  }


  private fun handleRegisterUser() {

    // check if the form is validated
    if (validateRegisterDetails()) {

      displayLoadingProgressDialog()

      // trim the beginning and ending whitespace
      val email = _binding.etEmail.text.toString().trim { it <= ' ' }
      val password = _binding.etPassword.text.toString().trim { it <= ' ' }


      FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(
          OnCompleteListener<AuthResult> { task ->

            // if the registration is successfully done
            if (task.isSuccessful) {

              // register user
              val firebaseUser: FirebaseUser = task.result!!.user!!

              // create our own application user entity
              val user = User(
                firebaseUser.uid,
                _binding.etFirstName.text.toString().trim { it <= ' ' },
                _binding.etLastName.text.toString().trim { it <= ' ' },
                email
              )

              _fireStoreService.registerUser(this@RegisterActivity, user)

            } else {
              hideLoadingProgressDialog()

              // registering is not successful
              displaySnackBar("Error registering", true)
            }
          }
        )

    }
  }


  fun onUserRegistrationSuccess() {

    hideLoadingProgressDialog()

    Toast.makeText(this@RegisterActivity, "You are registered successfully, you can now login", Toast.LENGTH_LONG)
      .show()

  }

}