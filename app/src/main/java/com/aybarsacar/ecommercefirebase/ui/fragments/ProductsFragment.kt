package com.aybarsacar.ecommercefirebase.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.FragmentProductsBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.ui.activities.AddProductActivity
import com.aybarsacar.ecommercefirebase.ui.adapters.MyProductsListAdapter


class ProductsFragment : BaseFragment() {

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

    return _binding.root
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    inflater.inflate(R.menu.add_product_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onResume() {
    super.onResume()
    getProductsFromFireStore()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    when (item.itemId) {
      R.id.action_add_product -> {
        startActivity(Intent(activity, AddProductActivity::class.java))
      }
    }

    return super.onOptionsItemSelected(item)
  }

  private fun getProductsFromFireStore() {
    displayLoadingProgressDialog()

    FirestoreService().getProductList(this)
  }


  /**
   * deletes the product given its id
   */
  fun deleteProduct(productId: String) {
    showAlertDialogDeleteProduct(productId)
  }


  fun handleProductDeleteSuccess() {
    hideLoadingProgressDialog()

    getProductsFromFireStore() // get the products again
  }


  fun handleProductDeleteFailure() {
    hideLoadingProgressDialog()
  }


  private fun showAlertDialogDeleteProduct(productId: String) {
    val builder = AlertDialog.Builder(requireActivity())

    builder.setTitle("Delete")

    val drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), R.color.red_error))

    builder.setMessage("Are you sure you want to delete $productId")
    builder.setIcon(wrappedDrawable)

    builder.setPositiveButton("Yes") { dialogInterface, _ ->

      displayLoadingProgressDialog()

      FirestoreService().deleteProduct(this@ProductsFragment, productId)

      dialogInterface.dismiss()
    }

    builder.setNegativeButton("No") { dialogInterface, _ ->
      dialogInterface.dismiss()
    }

    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
  }


  fun handleGetProductListSuccess(products: ArrayList<Product>) {
    hideLoadingProgressDialog()

    if (products.size > 0) {

      _binding.rvMyProductItems.visibility = View.VISIBLE
      _binding.tvNoProductsFound.visibility = View.GONE

      _binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
      _binding.rvMyProductItems.setHasFixedSize(true)

      // set the adapter for the recycler view
      _binding.rvMyProductItems.adapter = MyProductsListAdapter(requireActivity(), products, this@ProductsFragment)

    } else {
      _binding.tvNoProductsFound.visibility = View.VISIBLE
      _binding.rvMyProductItems.visibility = View.GONE
    }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    mBinding = null
  }
}