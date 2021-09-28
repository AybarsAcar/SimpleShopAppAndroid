package com.aybarsacar.ecommercefirebase.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aybarsacar.ecommercefirebase.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {

  private lateinit var _binding: ActivityAddProductBinding


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityAddProductBinding.inflate(layoutInflater)
    setContentView(_binding.root)
  }
}