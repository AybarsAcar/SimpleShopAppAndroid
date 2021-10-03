package com.aybarsacar.ecommercefirebase.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ItemCartLayoutBinding
import com.aybarsacar.ecommercefirebase.firestore.FirestoreService
import com.aybarsacar.ecommercefirebase.models.CartItem
import com.aybarsacar.ecommercefirebase.ui.activities.CartListActivity
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader


class CartItemsListAdapter(private val context: Context, private val cartItems: ArrayList<CartItem>) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class MyViewHolder(view: ItemCartLayoutBinding) : RecyclerView.ViewHolder(view.root) {
    val binding = view
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    val binding = ItemCartLayoutBinding.inflate(LayoutInflater.from(context), parent, false)

    return MyViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    val cartItem = cartItems[position]

    if (holder is MyViewHolder) {
      // display the image
      GlideLoader(context).loadProductPicture(cartItem.image, holder.binding.ivCartItemImage)

      // set the other properties of the view
      holder.binding.tvCartItemTitle.text = cartItem.title
      holder.binding.tvCartItemPrice.text = "AU$${cartItem.price}"
      holder.binding.tvCartQuantity.text = cartItem.cartQuantity


      if (cartItem.cartQuantity == "0") {
        holder.binding.ibRemoveCartItem.visibility = View.GONE
        holder.binding.ibAddCartItem.visibility = View.GONE

        holder.binding.tvCartQuantity.text = "OUT OF STOCK"
        holder.binding.tvCartQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_error))

      } else {
        holder.binding.ibRemoveCartItem.visibility = View.VISIBLE
        holder.binding.ibAddCartItem.visibility = View.VISIBLE

        holder.binding.tvCartQuantity.setTextColor(ContextCompat.getColor(context, R.color.primary))
      }

      holder.binding.ibAddCartItem.setOnClickListener {

        val cartQuantity = cartItem.cartQuantity.toInt()

        if (cartQuantity < cartItem.stockQuantity.toInt()) {

          val itemHashMap = HashMap<String, Any>()

          itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

          if (context is CartListActivity) {
            context.displayLoadingProgressDialog()
          }

          FirestoreService().updateCart(context, cartItem.id, itemHashMap)

        } else {

          if (context is CartListActivity) {
            context.displaySnackBar(
              "Available stock is ${cartItem.stockQuantity.toInt()}. You cannot add more than the available stock",
              true
            )
          }
        }
      }

      holder.binding.ibRemoveCartItem.setOnClickListener {
        // decrease the amount of items in the cart item
        if (cartItem.cartQuantity == "1") {
          FirestoreService().deleteItemFromCart(context, cartItem.id)
        } else {
          val cartQuantity = cartItem.cartQuantity.toInt()
          val itemHashMap = HashMap<String, Any>()

          itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

          if (context is CartListActivity) {
            context.displayLoadingProgressDialog()
          }

          FirestoreService().updateCart(context, cartItem.id, itemHashMap)
        }
      }

      holder.binding.ibDeleteCartItem.setOnClickListener {

        when (context) {
          is CartListActivity -> {
            context.displayLoadingProgressDialog()
          }
        }
        FirestoreService().deleteItemFromCart(context, cartItem.id)
      }
    }

  }

  override fun getItemCount(): Int {
    return cartItems.size
  }
}