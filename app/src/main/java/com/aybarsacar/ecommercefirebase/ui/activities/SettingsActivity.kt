package com.aybarsacar.ecommercefirebase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivitySettingsBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader
import com.google.firebase.auth.FirebaseAuth


class SettingsActivity : BaseActivity(), View.OnClickListener {

  private lateinit var _binding: ActivitySettingsBinding
  private lateinit var _fireStoreService: FirestoreService

  private lateinit var _userDetails: User


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivitySettingsBinding.inflate(layoutInflater)
    _fireStoreService = FirestoreService()

    setContentView(_binding.root)

    setupActionBar()

    // set on click listeners
    _binding.btnLogout.setOnClickListener(this)
    _binding.tvEdit.setOnClickListener(this)
  }


  private fun setupActionBar() {
    setSupportActionBar(_binding.toolbarSettingsActivity)

    // change the colour of our vector asset on runtime
    val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_ios_new_24)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.app_white))

    // set the back button in the action bar
    val actionBar = supportActionBar
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(drawable)
    }

    // set the navigation action to onBackPressed
    // same functionality as the back button of the device
    _binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
  }


  private fun getUserDetails() {
    displayLoadingProgressDialog()
    _fireStoreService.getUserById(this@SettingsActivity)
  }


  fun onUserDetailsSuccess(user: User) {
    // cache the user in
    _userDetails = user

    hideLoadingProgressDialog()

    // load the user image
    GlideLoader(this@SettingsActivity).loadUserImageAsUri(user.image, _binding.ivUserPhoto)

    _binding.tvName.text = "${user.firstName} ${user.lastName}"
    _binding.tvGender.text = user.gender
    _binding.tvEmail.text = user.email
    _binding.tvMobileNumber.text = user.mobile.toString()
  }

  fun onUserDetailsFailure() {
    hideLoadingProgressDialog()

    displaySnackBar("Error getting user information", true)
  }

  /**
   * called once the application is opened again
   */
  override fun onResume() {
    super.onResume()
    getUserDetails()
  }


  override fun onClick(view: View?) {
    if (view != null) {

      when (view.id) {

        R.id.btn_logout -> {
          // logout the user
          FirebaseAuth.getInstance().signOut()

          val intent = Intent(this@SettingsActivity, LoginActivity::class.java)

          // clear the user data and activities
          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          startActivity(intent)
          finish()
        }

        R.id.tv_edit -> {
          // move the user to the UserProfileActivity
          val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
          intent.putExtra(Constants.LOGGED_IN_USER_DETAILS, _userDetails)
          startActivity(intent)
          finish()
        }
      }
    }
  }
}