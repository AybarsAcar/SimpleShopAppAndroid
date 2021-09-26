package com.aybarsacar.ecommercefirebase.ui.activities

import android.app.Dialog
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
}