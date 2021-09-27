package com.aybarsacar.ecommercefirebase.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityLoginBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : BaseActivity(), View.OnClickListener {

  private lateinit var _binding: ActivityLoginBinding
  private lateinit var _fireStoreService: FirestoreService


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityLoginBinding.inflate(layoutInflater)
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

    // add on click listeners
    _binding.tvRegister.setOnClickListener(this)
    _binding.tvForgotPassword.setOnClickListener(this)
    _binding.btnLogin.setOnClickListener(this)
  }

  private fun validateLoginDetails(): Boolean {

    return when {

      TextUtils.isEmpty(_binding.etEmail.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter you email", true)
        false
      }

      TextUtils.isEmpty(_binding.etPassword.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter your password", true)
        false
      }

      // no form validation error
      else -> true
    }
  }


  private fun handleLoginUser() {
    if (validateLoginDetails()) {

      displayLoadingProgressDialog()

      // trim the beginning and ending whitespace
      val email = _binding.etEmail.text.toString().trim { it <= ' ' }
      val password = _binding.etPassword.text.toString().trim { it <= ' ' }


      FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
          OnCompleteListener<AuthResult> { task ->

            // if the registration is successfully done
            if (task.isSuccessful) {
              // send user to the main activity
              _fireStoreService.getUserById(this@LoginActivity)

            } else {
              hideLoadingProgressDialog()

              // registering is not successful
              displaySnackBar(task.exception!!.message.toString(), true)
            }
          }
        )

    }
  }


  /**
   * on click actions of the items on our view
   */
  override fun onClick(view: View?) {
    if (view != null) {

      when (view.id) {

        R.id.tv_register -> {
          val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
          startActivity(intent)
        }


        R.id.tv_forgot_password -> {
          val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
          startActivity(intent)
        }


        R.id.btn_login -> {
          handleLoginUser()
        }
      }
    }
  }


  fun userLoggedInSuccess(user: User) {

    hideLoadingProgressDialog()

    if (user.profileCompleted == 0) {

      val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)

      // user must be parcelable to be able to passed in with intent
      intent.putExtra(Constants.LOGGED_IN_USER_DETAILS, user)

      startActivity(intent)
    } else {
      startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
    }

    finish()
  }
}