package com.aybarsacar.ecommercefirebase.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aybarsacar.ecommercefirebase.databinding.ItemDashboardLayoutBinding
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.utils.helpers.GlideLoader


class DashboardItemsListAdapter(private val context: Context, private val list: ArrayList<Product>) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var _onClickListener: OnClickListener? = null

  class MyViewHolder(view: ItemDashboardLayoutBinding) : RecyclerView.ViewHolder(view.root) {
    val binding = view
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    val binding = ItemDashboardLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
    return MyViewHolder(binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    val model = list[position]

    if (holder is MyViewHolder) {
      // display the image
      GlideLoader(context).loadProductPicture(model.image, holder.binding.ivDashboardItemImage)

      // set the other properties of the view
      holder.binding.tvDashboardItemTitle.text = model.title
      holder.binding.tvDashboardItemPrice.text = "AU$${model.price}"

      holder.binding.root.setOnClickListener {
        if (_onClickListener != null) {
          _onClickListener!!.onClick(position, model)
        }
      }
    }
  }


  fun setOnclickListener(onClickListener: OnClickListener) {
    _onClickListener = onClickListener
  }


  override fun getItemCount(): Int {
    return list.size
  }


  interface OnClickListener {
    fun onClick(position: Int, product: Product)
  }
}

