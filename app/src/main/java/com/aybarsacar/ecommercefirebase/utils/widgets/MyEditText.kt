package com.aybarsacar.ecommercefirebase.utils.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


class MyEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

  init {
    applyFont()
  }


  private fun applyFont() {

    val typeface: Typeface = Typeface.createFromAsset(context.assets, "Lato-Regular.ttf")

    setTypeface(typeface)
  }
}