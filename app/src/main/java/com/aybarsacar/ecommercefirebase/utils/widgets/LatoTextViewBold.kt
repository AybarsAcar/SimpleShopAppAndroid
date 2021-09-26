package com.aybarsacar.ecommercefirebase.utils.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 * can be usable to create text views
 */
class LatoTextViewBold(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

  init {
    applyFont()
  }


  private fun applyFont() {

    val typeface: Typeface = Typeface.createFromAsset(context.assets, "Lato-Bold.ttf")

    setTypeface(typeface)
  }
}