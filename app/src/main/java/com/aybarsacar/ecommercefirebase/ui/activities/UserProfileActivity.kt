package com.aybarsacar.ecommercefirebase.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityUserProfileBinding
import com.aybarsacar.ecommercefirebase.databinding.DialogCustomImageSelectionBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader
import java.io.File
import java.io.OutputStream
import java.util.*
import kotlin.collections.HashMap


class UserProfileActivity : BaseActivity(), View.OnClickListener {

  companion object {
    private const val IMAGE_DIRECTORY = "ECommApp"
  }

  private lateinit var _binding: ActivityUserProfileBinding
  private lateinit var _fireStoreService: FirestoreService


  // class variables
  private var _userDetails = User() // currently logged in user
  private var _selectedImageUri: Uri? = null
  private var _userProfileImageUrl: String = ""

  // image permissions
  val _getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    if (result.resultCode == Activity.RESULT_OK) {
      try {
        // get image from user gallery
        val selectedImageUri: Uri? = result.data?.data

        if (selectedImageUri != null) {
          GlideLoader(this).loadUserImageAsUri(selectedImageUri, _binding.ivUserPhoto)

          // cache so the image can be saved to the db on submit
          _selectedImageUri = selectedImageUri
        } else {
          result.data?.let {
            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
            GlideLoader(this).loadUserImageAsBitmap(bitmap, _binding.ivUserPhoto)

            // cache so the image can be saved to the db on submit
            _selectedImageUri = saveImageToGallery(bitmap)
          }
        }

      } catch (e: Exception) {

        Log.e("Error", e.localizedMessage!!)
      }

    } else if (result.resultCode == Activity.RESULT_CANCELED) {
      Log.e("Cancelled", "User cancelled image selection")
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityUserProfileBinding.inflate(layoutInflater)
    _fireStoreService = FirestoreService()

    setContentView(_binding.root)


    // if the user information is passed in
    if (intent.hasExtra(Constants.LOGGED_IN_USER_DETAILS)) {
      _userDetails = intent.getParcelableExtra<User>(Constants.LOGGED_IN_USER_DETAILS)!!
    }

    if (_userDetails.profileCompleted == 0) {
      // user just signed up and logged in for the first time
      _binding.tvTitle.text = "Complete Profile"

      _binding.etFirstName.isEnabled = false
      _binding.etLastName.isEnabled = false
    } else {
      // user editing profile
      setupActionBar()
      _binding.tvTitle.text = "Edit Profile"

      // load the user image
      GlideLoader(this@UserProfileActivity).loadUserImageAsUri(_userDetails.image, _binding.ivUserPhoto)

      _binding.etMobileNumber.setText(_userDetails.mobile.toString())
      _binding.rbMale.isChecked = _userDetails.gender == Constants.MALE
      _binding.rbFemale.isChecked = _userDetails.gender == Constants.FEMALE
    }


    _binding.etEmail.isEnabled = false
    // populate the form
    _binding.etFirstName.setText(_userDetails.firstName)
    _binding.etLastName.setText(_userDetails.lastName)
    _binding.etEmail.setText(_userDetails.email)

    // add click listeners
    _binding.ivAddPhoto.setOnClickListener(this)
    _binding.btnSave.setOnClickListener(this)
  }


  private fun displayRationalDialogForPermission() {
    AlertDialog.Builder(this)
      .setMessage("It looks like you have turned off permissions required for this feature. It can be enabled from the Application Settings")
      .setPositiveButton("Go to Settings") { _, _ ->
        try {

          // go to user's settings
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

          // application link in user's settings
          val uri = Uri.fromParts("package", packageName, null)
          intent.data = uri

          startActivity(intent)

        } catch (e: ActivityNotFoundException) {
          Toast.makeText(this, "Problem finding settings", Toast.LENGTH_SHORT).show()
          e.printStackTrace()
        }
      }
      .setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }


  private fun displayImageSelectionDialog() {

    val dialog = Dialog(this)
    val dialogBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)

    dialog.setContentView(dialogBinding.root)

    dialogBinding.tvCamera.setOnClickListener {

      if (ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        // we have permission open the camera application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        _getImage.launch(intent)

        dialog.dismiss()
      } else {
        displayRationalDialogForPermission()
      }
    }

    dialogBinding.tvGallery.setOnClickListener {

      if (ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        // we have permission open the user photo gallery
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        _getImage.launch(intent)

        dialog.dismiss()
      } else {
        displayRationalDialogForPermission()
      }

    }

    dialog.show()
  }

  private fun validateUserProfileDetails(): Boolean {
    return when {

      TextUtils.isEmpty(_binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Mobile number is required to continue", true)
        false
      }

      else -> true
    }
  }

  private fun saveImageToGallery(bitmap: Bitmap): Uri? {
    val fos: OutputStream

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_" + ".jpg")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        contentValues.put(
          MediaStore.MediaColumns.RELATIVE_PATH,
          Environment.DIRECTORY_PICTURES + File.separator + IMAGE_DIRECTORY
        )

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        fos = resolver.openOutputStream(Objects.requireNonNull(imageUri!!))!!

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

        Objects.requireNonNull<OutputStream>(fos)

        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()

        return imageUri
      }
    } catch (e: Exception) {
      Toast.makeText(this, "Problem saving the image", Toast.LENGTH_SHORT).show()
    }

    return null
  }


  fun onUserProfileUpdatedSuccess() {
    hideLoadingProgressDialog()
    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

    // send the user to the main activity
    startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
    finish()
  }

  override fun onClick(view: View?) {
    if (view != null) {
      when (view.id) {

        R.id.iv_add_photo -> {
          displayImageSelectionDialog()
          return
        }

        R.id.btn_save -> {
          if (validateUserProfileDetails()) {
            displayLoadingProgressDialog()

            if (_selectedImageUri != null) {
              _fireStoreService.uploadProfileImageToCloudStorage(this, _selectedImageUri)
            } else {
              updateUserProfileDetails()
            }
          }
          return
        }
      }
    }
  }


  private fun updateUserProfileDetails() {
    // fields we will be sending while updating the user details
    val userHashMap = HashMap<String, Any>()

    val mobileNumber = _binding.etMobileNumber.text.toString().trim { it <= ' ' }

    val gender = if (_binding.rbMale.isChecked) {
      Constants.MALE
    } else {
      Constants.FEMALE
    }

    if (_userProfileImageUrl.isNotEmpty()) {
      userHashMap[Constants.IMAGE] = _userProfileImageUrl
    }

    val firstName = _binding.etFirstName.text.toString().trim { it <= ' ' }
    if (firstName != _userDetails.firstName) {
      userHashMap[Constants.FIRST_NAME] = firstName
    }

    val lastName = _binding.etLastName.text.toString().trim { it <= ' ' }
    if (lastName != _userDetails.lastName) {
      userHashMap[Constants.LAST_NAME] = lastName
    }

    if (mobileNumber != _userDetails.mobile.toString()) {
      userHashMap[Constants.MOBILE] = mobileNumber.toLong()
    }

    if (gender != _userDetails.gender) {
      userHashMap[Constants.GENDER] = gender
    }

    userHashMap[Constants.PROFILE_COMPLETED] = 1

    _fireStoreService.updateUserDetails(this, userHashMap)
  }


  fun onImageUploadSuccess(imageUrl: String) {
    // cache the url from the firebase in the class which will be saved on submit
    _userProfileImageUrl = imageUrl

    // then update the userprofile details
    updateUserProfileDetails()
  }

  fun onImageUploadFailure() {
    hideLoadingProgressDialog()

    displaySnackBar("Error uploading image", true)
  }

  private fun setupActionBar() {
    setSupportActionBar(_binding.toolbarUserProfile)

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
    _binding.toolbarUserProfile.setNavigationOnClickListener { onBackPressed() }
  }
}