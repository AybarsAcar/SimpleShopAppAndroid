package com.aybarsacar.ecommercefirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.FragmentProductsBinding
import com.aybarsacar.ecommercefirebase.ui.activities.AddProductActivity


class ProductsFragment : Fragment() {

  private var mBinding: FragmentProductsBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val _binding get() = mBinding!!


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // if we want to use the option menu in fragment we need to add it
    setHasOptionsMenu(true)
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    mBinding = FragmentProductsBinding.inflate(inflater, container, false)

    _binding.textHome.text = "Products Fragment"

    return _binding.root
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    inflater.inflate(R.menu.add_product_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    when (id) {
      R.id.action_add_product -> {
        startActivity(Intent(activity, AddProductActivity::class.java))
      }
    }

    return super.onOptionsItemSelected(item)
  }


  override fun onDestroyView() {
    super.onDestroyView()
    mBinding = null
  }
}