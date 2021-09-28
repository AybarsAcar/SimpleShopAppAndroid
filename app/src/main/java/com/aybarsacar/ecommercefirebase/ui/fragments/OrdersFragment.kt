package com.aybarsacar.ecommercefirebase.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aybarsacar.ecommercefirebase.databinding.FragmentOrdersBinding


class OrdersFragment : Fragment() {

  private var mBinding: FragmentOrdersBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val _binding get() = mBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mBinding = FragmentOrdersBinding.inflate(inflater, container, false)

    _binding.textNotifications.text = "Orders Fragment"

    return _binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    mBinding = null
  }
}