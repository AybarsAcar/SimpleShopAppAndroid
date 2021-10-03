package com.aybarsacar.ecommercefirebase.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.ecommercefirebase.databinding.ItemListLayoutBinding
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.ui.activities.ProductDetailsActivity
import com.aybarsacar.ecommercefirebase.ui.fragments.ProductsFragment
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader


open class MyProductsListAdapter(
  private val context: Context,
  private val list: ArrayList<Product>,
  private val fragment: ProductsFragment
) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  class MyViewHolder(view: ItemListLayoutBinding) : RecyclerView.ViewHolder(view.root) {
    val binding = view
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    val binding = ItemListLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    return MyViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    val model = list[position]

    if (holder is MyViewHolder) {
      // display the image
      GlideLoader(context).loadProductPicture(model.image, holder.binding.ivItemImage)

      // set the other properties of the view
      holder.binding.tvItemName.text = model.title
      holder.binding.tvItemPrice.text = "AU$${model.price}"


      // add an onClickListener to the button delete
      holder.binding.ibDeleteProduct.setOnClickListener {
        // delete product by its id
        fragment.deleteProduct(model.id)
      }

      // add onClickListener to go to the details page
      holder.binding.root.setOnClickListener {
        val intent = Intent(context, ProductDetailsActivity::class.java)

        // pass in the product id to the intent
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.id)
        intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.userId)

        context.startActivity(intent)
      }
    }
  }

  override fun getItemCount(): Int {
    return list.size
  }

}