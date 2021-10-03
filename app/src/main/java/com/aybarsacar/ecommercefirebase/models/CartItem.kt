package com.aybarsacar.ecommercefirebase.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CartItem(
  val userId: String = "",
  val productId: String = "",
  val title: String = "",
  val price: String = "",
  val image: String = "",
  var cartQuantity: String = "",
  var stockQuantity: String = "",
  var id: String = "",
) : Parcelable