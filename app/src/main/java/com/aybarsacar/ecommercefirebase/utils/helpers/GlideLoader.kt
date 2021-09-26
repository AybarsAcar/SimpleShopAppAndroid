package com.aybarsacar.ecommercefirebase.utils.helpers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.aybarsacar.ecommercefirebase.R
import com.bumptech.glide.Glide
import java.io.IOException

class GlideLoader(val context: Context) {

  fun loadUserImageAsUri(imageUri: Uri, imageView: ImageView) {

    try {
      Glide
        .with(context)
        .load(imageUri)
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

}