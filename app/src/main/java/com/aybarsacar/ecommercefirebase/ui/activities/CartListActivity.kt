package com.aybarsacar.ecommercefirebase.ui.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityCartListBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.CartItem
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.ui.adapters.CartItemsListAdapter


class CartListActivity : BaseActivity() {

  private lateinit var _binding: ActivityCartListBinding

  private lateinit var _products: ArrayList<Product>
  private lateinit var _cartItems: ArrayList<CartItem>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityCartListBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    setupActionBar()
  }

  override fun onResume() {
    super.onResume()

    getProducts()
  }


  private fun getCartItemsList() {
    displayLoadingProgressDialog()

    FirestoreService().getCartItemsList(this@CartListActivity)
  }


  fun handleSuccessCartItemsList(cartItems: ArrayList<CartItem>) {

    _cartItems = cartItems

    hideLoadingProgressDialog()

    for (product in _products) {
      for (cartItem in _cartItems) {
        if (product.id == cartItem.productId) {
          cartItem.stockQuantity = product.stockQuantity

          if (product.stockQuantity.toInt() == 0) {
            cartItem.cartQuantity = product.stockQuantity
          }
        }
      }
    }

    if (_cartItems.size > 0) {
      _binding.rvCartItemsList.visibility = View.VISIBLE
      _binding.llCheckout.visibility = View.VISIBLE
      _binding.tvNoCartItemFound.visibility = View.GONE

      _binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)

      val adapter = CartItemsListAdapter(this, cartItems)
      _binding.rvCartItemsList.adapter = adapter

      var subTotal = 0.0
      for (item in _cartItems) {

        val availableQuantity = item.stockQuantity.toInt()

        if (availableQuantity > 0) {
          subTotal += (item.price.toDouble() * item.cartQuantity.toInt())
        }
      }

      _binding.tvSubTotal.text = "AU$ $subTotal"

      _binding.tvShippingCharge.text = "AU$ 10.0"

      if (subTotal > 0) {
        _binding.llCheckout.visibility = View.VISIBLE
        _binding.tvTotalAmount.text = "AU$ ${subTotal + 10}"
      } else {
        _binding.llCheckout.visibility = View.GONE
      }

    } else {
      _binding.tvNoCartItemFound.visibility = View.VISIBLE
      _binding.rvCartItemsList.visibility = View.GONE
      _binding.llCheckout.visibility = View.GONE

    }
  }


  private fun setupActionBar() {

    setSupportActionBar(_binding.toolbarCartListActivity)

    // change the colour of our vector asset on runtime
    val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_ios_new_24)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.app_white))

    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    _binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
  }


  private fun getProducts() {
    displayLoadingProgressDialog()
    FirestoreService().getAllProductsList(this)
  }


  fun handleGetAllProductsListSuccess(products: ArrayList<Product>) {
    hideLoadingProgressDialog()
    _products = products

    getCartItemsList()
  }


  fun handleCartItemDeletedSuccess() {
    hideLoadingProgressDialog()
    displayInfoSnackBar("Item removed successfully")

    getCartItemsList()
  }
}