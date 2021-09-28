package com.aybarsacar.ecommercefirebase.ui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : BaseActivity() {

  private lateinit var _binding: ActivityDashboardBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityDashboardBinding.inflate(layoutInflater)
    setContentView(_binding.root)

    supportActionBar!!.setBackgroundDrawable(
      ContextCompat.getDrawable(
        this@DashboardActivity,
        R.drawable.menu_gradient_color_background
      )
    )

    val navView: BottomNavigationView = _binding.navView

    val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    val appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.navigation_dashboard, R.id.navigation_products, R.id.navigation_orders
      )
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
  }


  /**
   * override the default onBackPressed
   * user needs to press the back key twice to go back
   */
  override fun onBackPressed() {
    handleDoubleBackToExit()
  }
}