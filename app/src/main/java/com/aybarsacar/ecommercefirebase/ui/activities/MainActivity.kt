package com.aybarsacar.ecommercefirebase.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aybarsacar.ecommercefirebase.databinding.ActivityMainBinding
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants


class MainActivity : AppCompatActivity() {

  private lateinit var _binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    val sharedPreferences = getSharedPreferences(Constants.MY_SHOP_PREFERENCES, Context.MODE_PRIVATE)
    val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")


    _binding.tvMain.text = "Hello ${username}!"

  }

}