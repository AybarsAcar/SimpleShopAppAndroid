package com.aybarsacar.ecommercefirebase.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityUserProfileBinding
import com.aybarsacar.ecommercefirebase.databinding.DialogCustomImageSelectionBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
  private var _imagePath = ""

  // image permissions
  val _getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    if (result.resultCode == Activity.RESULT_OK) {
      try {
        // get image from user gallery
        val selectedImageUri: Uri? = result.data?.data

        if (selectedImageUri != null) {
          GlideLoader(this).loadUserImageAsUri(selectedImageUri, _binding.ivUserPhoto)
          _imagePath = selectedImageUri.toString()
        } else {
          result.data?.let {
            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
            GlideLoader(this).loadUserImageAsBitmap(bitmap, _binding.ivUserPhoto)
            _imagePath = saveImageToInternalStorage(bitmap)
          }
        }

      } catch (e: Exception) {

        Log.e("Error", e.localizedMessage!!)
      }

    } else if (result.resultCode == Activity.RESULT_CANCELED) {
      Log.e("Cancelled", "User cancelled image celection")
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

    _binding.etFirstName.isEnabled = false
    _binding.etLastName.isEnabled = false
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

  private fun saveImageToInternalStorage(bitmap: Bitmap): String {

    // give our application context to the image so the user can filter
    val wrapper = ContextWrapper(applicationContext)

    var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
    file = File(file, "${UUID.randomUUID()}.jpg")

    val stream: FileOutputStream
    try {
      stream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      stream.flush()
      stream.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }


    return file.absolutePath
  }

  fun onUserProfileUpdatedSuccess() {
    hideLoadingProgressDialog()
    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

    // send the user to the main activity
    startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
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

            // fields we will be sending while updating the user details
            val userHashMap = HashMap<String, Any>()

            val mobileNumber = _binding.etMobileNumber.text.toString().trim { it <= ' ' }

            val gender = if (_binding.rbMale.isChecked) {
              Constants.MALE
            } else {
              Constants.FEMALE
            }

            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
            userHashMap[Constants.GENDER] = gender

            displayLoadingProgressDialog()

            _fireStoreService.updateUserDetails(this, userHashMap)
          }
          return
        }
      }
    }
  }
}