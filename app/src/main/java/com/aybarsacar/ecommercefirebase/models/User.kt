package com.aybarsacar.ecommercefirebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * our User Entity
 */
@Parcelize
data class User(
  val id: String = "",
  val firstName: String = "",
  val lastName: String = "",
  val email: String = "",
  val image: String = "",
  val mobile: Long = 0,
  val gender: String = "",
  val profileCompleted: Int = 0,
) : Parcelable