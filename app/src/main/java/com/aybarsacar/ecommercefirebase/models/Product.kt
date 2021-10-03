package com.aybarsacar.ecommercefirebase.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Product(

  val userId: String = "",
  val username: String = "",
  val title: String = "",
  val price: String = "",
  val description: String = "",
  val stockQuantity: String = "",
  val image: String = "",
  var id: String = "",

  ) : Parcelable