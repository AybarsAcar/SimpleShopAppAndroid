package com.aybarsacar.ecommercefirebase.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
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
import com.aybarsacar.ecommercefirebase.databinding.ActivityAddProductBinding
import com.aybarsacar.ecommercefirebase.databinding.DialogCustomImageSelectionBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader
import java.io.File
import java.io.OutputStream
import java.util.*

class AddProductActivity : BaseActivity(), View.OnClickListener {

  companion object {
    private const val IMAGE_DIRECTORY = "ECommApp"
  }

  private lateinit var _binding: ActivityAddProductBinding
  private lateinit var _fireStore: FirestoreService

  private var _selectedImageUri: Uri? = null
  private var _productImageUrl: String = ""


  // image permissions
  val _getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    if (result.resultCode == Activity.RESULT_OK) {
      try {
        // get image from user gallery
        val selectedImageUri: Uri? = result.data?.data

        if (selectedImageUri != null) {
          GlideLoader(this).loadUserImageAsUri(selectedImageUri, _binding.ivProductImage)

          // cache so the image can be saved to the db on submit
          _selectedImageUri = selectedImageUri

        } else {
          result.data?.let {
            val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
            GlideLoader(this).loadUserImageAsBitmap(bitmap, _binding.ivProductImage)

            // cache so the image can be saved to the db on submit
            _selectedImageUri = saveImageToGallery(bitmap)
          }
        }

        _binding.ivAddUpdateProductImage.setImageDrawable(
          ContextCompat.getDrawable(
            this,
            R.drawable.ic_baseline_edit_24
          )
        )

      } catch (e: Exception) {

        Log.e("Error", e.localizedMessage!!)
      }

    } else if (result.resultCode == Activity.RESULT_CANCELED) {
      Log.e("Cancelled", "User cancelled image selection")
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _fireStore = FirestoreService()
    _binding = ActivityAddProductBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    setupActionBar()

    _binding.ivAddUpdateProductImage.setOnClickListener(this)
    _binding.btnAddProduct.setOnClickListener(this)
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


  private fun setupActionBar() {
    setSupportActionBar(_binding.toolbarAddProductActivity)

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
    _binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
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


  private fun validateAddProductForm(): Boolean {
    return when {

      TextUtils.isEmpty(_binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter a title for the product", true)
        false
      }

      TextUtils.isEmpty(_binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter a price for the product", true)
        false
      }

      TextUtils.isEmpty(_binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter a description for the product", true)
        false
      }

      TextUtils.isEmpty(_binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
        displaySnackBar("Please enter a quantity for the product", true)
        false
      }

      _selectedImageUri == null -> {
        displaySnackBar("Please select an image for the product", true)
        false
      }

      else -> true
    }
  }


  fun onImageUploadSuccess(imageUrl: String) {
    // cache the url from the firebase in the class which will be saved on submit
    _productImageUrl = imageUrl

    hideLoadingProgressDialog()
    // then create the product
  }

  fun onImageUploadFailure() {
    hideLoadingProgressDialog()

    displaySnackBar("Error uploading image", true)
  }


  override fun onClick(view: View?) {

    if (view != null) {
      when (view.id) {

        R.id.iv_add_update_product_image -> {
          displayImageSelectionDialog()
          return
        }

        R.id.btn_add_product -> {

          if (validateAddProductForm()) {
            // save image
            displayLoadingProgressDialog()
            _fireStore.uploadImageToCloudStorage(this, _selectedImageUri, Constants.PRODUCT_IMAGE)

          }
        }
      }
    }
  }
}