package com.aybarsacar.ecommercefirebase.utils.helpers

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.aybarsacar.ecommercefirebase.R
import com.bumptech.glide.Glide
import java.io.IOException

class GlideLoader(val context: Context) {

  fun loadUserImageAsUri(image: Any, imageView: ImageView) {

    try {
      Glide
        .with(context)
        .load(image)
        .centerCrop()
        .placeholder(R.drawable.default_avatar)
        .into(imageView)

    } catch (e: IOException) {
      e.printStackTrace()
    }
  }


  fun loadUserImageAsBitmap(imageBitmap: Bitmap, imageView: ImageView) {

    try {
      Glide
        .with(context)
        .load(imageBitmap)
        .centerCrop()
        .placeholder(R.drawable.default_avatar)
        .into(imageView)

    } catch (e: IOException) {
      e.printStackTrace()
    }
  }


  fun loadProductPicture(image: Any, imageView: ImageView) {

    try {
      Glide
        .with(context)
        .load(image)
        .centerCrop()
        .into(imageView)

    } catch (e: IOException) {
      e.printStackTrace()
    }
  }

}