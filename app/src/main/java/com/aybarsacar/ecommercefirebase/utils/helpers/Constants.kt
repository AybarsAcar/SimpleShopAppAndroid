package com.aybarsacar.ecommercefirebase.utils.helpers

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap


object Constants {

  // fire store cloud collections
  const val USERS: String = "users"
  const val PRODUCTS = "products"

  const val MY_SHOP_PREFERENCES: String = "MyShopPreferences"
  const val LOGGED_IN_USERNAME: String = "logged_in_username"

  const val LOGGED_IN_USER_DETAILS: String = "logged_in_user_details"

  const val MALE: String = "Male"
  const val FEMALE: String = "Female"

  // database fields in firebase
  const val MOBILE = "mobile"
  const val GENDER = "gender"
  const val IMAGE = "image"
  const val PROFILE_COMPLETED = "profileCompleted"
  const val FIRST_NAME = "firstName"
  const val LAST_NAME = "lastName"

  const val USER_PROFILE_IMAGE = "User_Profile_Image"

  const val PRODUCT_IMAGE = "Product_Image"

  const val USER_ID = "userId"


  // passed into the details activity through the intent
  const val EXTRA_PRODUCT_ID = "extra_product_id"

  const val EXTRA_PRODUCT_OWNER_ID = "extra_product_owner_id"

  const val DEFAULT_CART_QUANTITY = "1"

  const val CART_ITEMS = "cart_items"

  // product id we are currently looking at
  const val PRODUCT_ID = "productId"


  /**
   * returns the file extension of the image uri
   */
  fun getFileExtension(activity: Activity, uri: Uri?): String? {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
  }
}