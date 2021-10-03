package com.aybarsacar.ecommercefirebase.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.DialogProgressBinding


/**
 * Base fragment of our other fragments
 */
open class BaseFragment : Fragment() {

  private lateinit var _progressDialog: Dialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_base, container, false)
  }


  protected fun displayLoadingProgressDialog(message: String = resources.getString(R.string.please_wait)) {

    val dialogBinding = DialogProgressBinding.inflate(layoutInflater)

    _progressDialog = Dialog(requireActivity())

    // set the screen content from teh layout resource
    // the resource will be inflated, adding all top-level views to the screen
    _progressDialog.setContentView(dialogBinding.root)

    dialogBinding.tvProgressText.text = message

    _progressDialog.setCancelable(false)
    _progressDialog.setCanceledOnTouchOutside(false)

    _progressDialog.show()
  }


  protected fun hideLoadingProgressDialog() {
    _progressDialog.dismiss()
  }

}