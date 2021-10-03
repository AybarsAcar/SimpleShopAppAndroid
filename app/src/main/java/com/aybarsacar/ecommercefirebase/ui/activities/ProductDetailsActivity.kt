package com.aybarsacar.ecommercefirebase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityProductDetailsBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.CartItem
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader


class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

  private lateinit var _binding: ActivityProductDetailsBinding
  private lateinit var _fireStoreService: FirestoreService

  private var _productId: String = ""
  private var _productOwnerId: String = ""

  private lateinit var _productDetails: Product

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _fireStoreService = FirestoreService()
    _binding = ActivityProductDetailsBinding.inflate(layoutInflater)

    setContentView(_binding.root)

    setupActionBar()

    // cache the productId from the intent
    if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
      _productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

      getProductDetails(_productId)
    }

    // cache the owner of the product
    if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
      _productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
    }


    if (_fireStoreService.getCurrentUserId() == _productOwnerId) {
      // do not display add to cart button
      _binding.btnAddToCart.visibility = View.GONE
      _binding.btnGoToMyCart.visibility = View.GONE
    } else {
      _binding.btnAddToCart.visibility = View.VISIBLE
    }

    _binding.btnAddToCart.setOnClickListener(this)
  }


  private fun getProductDetails(id: String) {
    displayLoadingProgressDialog()
    _fireStoreService.getProductDetails(this, id)
  }


  private fun setupActionBar() {

    setSupportActionBar(_binding.toolbarProductDetailsActivity)

    // change the colour of our vector asset on runtime
    val drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_ios_new_24)
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.app_white))

    val actionBar = supportActionBar

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    _binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
  }

  fun handleProductDetailsSuccess(product: Product) {

    _productDetails = product

    // set the fields
    GlideLoader(this).loadProductPicture(product.image, _binding.ivProductDetailImage)
    _binding.tvProductDetailsTitle.text = product.title
    _binding.tvProductDetailsPrice.text = product.price
    _binding.tvProductDetailsDescription.text = product.description
    _binding.tvProductDetailsAvailableQuantity.text = product.stockQuantity

    if (product.stockQuantity.toInt() == 0) {
      hideLoadingProgressDialog()
      _binding.btnAddToCart.visibility = View.GONE
      _binding.tvProductDetailsAvailableQuantity.text = "OUT OF STOCK"
      _binding.tvProductDetailsAvailableQuantity.setTextColor(ContextCompat.getColor(this, R.color.red_error))
    } else {

      if (_fireStoreService.getCurrentUserId() == product.userId) {
        hideLoadingProgressDialog()
      } else {
        _fireStoreService.itemExists(this, _productId)
      }
    }
  }

  fun handleProductDetailsFailure() {
    hideLoadingProgressDialog()

    displaySnackBar("Error loading the product", true)
  }


  private fun addToCart() {
    val itemToAddToCart = CartItem(
      FirestoreService().getCurrentUserId(),
      _productId,
      _productDetails.title,
      _productDetails.price,
      _productDetails.image,
      Constants.DEFAULT_CART_QUANTITY,
    )

    displayLoadingProgressDialog()

    _fireStoreService.addCartItems(this, itemToAddToCart)
  }


  fun handleAddCartItemsSuccess() {
    hideLoadingProgressDialog()
    displaySnackBar("Item added to your cart successfully", false)

    _binding.btnAddToCart.visibility = View.GONE
    _binding.btnGoToMyCart.visibility = View.VISIBLE
  }


  fun handleAddCartItemsFailure() {
    hideLoadingProgressDialog()
  }


  fun handleItemExistsSuccess() {
    hideLoadingProgressDialog()

    _binding.btnAddToCart.visibility = View.GONE
    _binding.btnGoToMyCart.visibility = View.VISIBLE
  }


  override fun onClick(view: View?) {
    if (view != null) {
      when (view.id) {

        R.id.btn_add_to_cart -> {
          addToCart()
        }

        R.id.btn_go_to_my_cart -> {
          startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }
      }
    }
  }
}