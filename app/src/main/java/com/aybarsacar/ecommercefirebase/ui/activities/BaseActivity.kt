package com.aybarsacar.ecommercefirebase.ui.activities

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar

/**
 * base activity to handle pop ups etc, common things
 * our activities will inherit from this activity
 * we can put common functionality in this class's onCreate method if we like to
 */
open class BaseActivity : AppCompatActivity() {

  private lateinit var _progressDialog: Dialog

  private var _doubleBackToExitPressedOnce = false


  protected fun displaySnackBar(message: String, errorMessage: Boolean) {

    val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
    val snackBarView = snackBar.view

    if (errorMessage) {
      snackBarView.setBackgroundColor(
        ContextCompat.getColor(this@BaseActivity, R.color.red_error)
      )
    } else {
      snackBarView.setBackgroundColor(
        ContextCompat.getColor(this@BaseActivity, R.color.green_success)
      )
    }

    snackBar.show()
  }

  protected fun displayInfoSnackBar(message: String) {

    val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
    val snackBarView = snackBar.view

    snackBarView.setBackgroundColor(
      ContextCompat.getColor(this@BaseActivity, R.color.primary_light)
    )

    snackBar.show()
  }


  protected fun displayLoadingProgressDialog(message: String = resources.getString(R.string.please_wait)) {

    val dialogBinding = DialogProgressBinding.inflate(layoutInflater)

    _progressDialog = Dialog(this)

    // set the screen content from teh layout resource
    // the resource will be inflated, adding all top-level views to the screen
    _progressDialog.setContentView(dialogBinding.root)

    dialogBinding.tvProgressText.text = message

    _progressDialog.setCancelable(false)
    _progressDialog.setCanceledOnTouchOutside(false)

    _progressDialog.show()
  }


  fun hideLoadingProgressDialog() {
    _progressDialog.dismiss()
  }


  protected fun handleDoubleBackToExit() {
    if (_doubleBackToExitPressedOnce) {
      super.onBackPressed()
      return
    }

    _doubleBackToExitPressedOnce = true

//    Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
    displayInfoSnackBar(resources.getString(R.string.please_click_back_again_to_exit))

    // reset back to false after 2 seconds
    Handler(Looper.getMainLooper()).postDelayed({
      _doubleBackToExitPressedOnce = false
    }, 2000)

  }
}