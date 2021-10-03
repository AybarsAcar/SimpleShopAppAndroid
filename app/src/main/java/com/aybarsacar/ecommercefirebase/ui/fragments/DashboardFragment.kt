package com.aybarsacar.ecommercefirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.FragmentDashboardBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.ui.activities.CartListActivity
import com.aybarsacar.ecommercefirebase.ui.activities.ProductDetailsActivity
import com.aybarsacar.ecommercefirebase.ui.activities.SettingsActivity
import com.aybarsacar.ecommercefirebase.ui.adapters.DashboardItemsListAdapter
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants


class DashboardFragment : BaseFragment() {

  private var mBinding: FragmentDashboardBinding? = null

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
    mBinding = FragmentDashboardBinding.inflate(inflater, container, false)

    return _binding.root
  }

  override fun onResume() {
    super.onResume()

    getDashboardItemsList()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    inflater.inflate(R.menu.dashboard_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    when (id) {
      R.id.action_settings -> {
        startActivity(Intent(activity, SettingsActivity::class.java))
      }

      R.id.action_my_cart -> {
        startActivity(Intent(activity, CartListActivity::class.java))
      }
    }

    return super.onOptionsItemSelected(item)
  }


  override fun onDestroyView() {
    super.onDestroyView()
    mBinding = null
  }


  private fun getDashboardItemsList() {
    displayLoadingProgressDialog()
    FirestoreService().getDashboardItemsList(this@DashboardFragment)
  }


  fun handleGetDashboardItemsListFailure() {
    hideLoadingProgressDialog()
  }

  fun handleGetDashboardItemsListSuccess(products: ArrayList<Product>) {

    hideLoadingProgressDialog()

    // display the products
    if (products.size > 0) {
      _binding.rvDashboardItems.visibility = View.VISIBLE
      _binding.tvNoDashboardItemsFound.visibility = View.GONE

      _binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
      _binding.rvDashboardItems.setHasFixedSize(true)

      // set the adapter for the recycler view
      val adapter = DashboardItemsListAdapter(requireActivity(), products)
      _binding.rvDashboardItems.adapter = adapter

      adapter.setOnclickListener(object : DashboardItemsListAdapter.OnClickListener {
        override fun onClick(position: Int, product: Product) {
          val intent = Intent(context, ProductDetailsActivity::class.java)

          intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.id)
          intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.userId)

          startActivity(intent)
        }

      })

    } else {
      _binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
      _binding.rvDashboardItems.visibility = View.GONE
    }
  }
}