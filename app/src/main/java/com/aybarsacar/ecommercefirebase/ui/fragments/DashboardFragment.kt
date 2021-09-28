package com.aybarsacar.ecommercefirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.aybarsacar.ecommercefirebase.R
import com.aybarsacar.ecommercefirebase.databinding.FragmentDashboardBinding
import com.aybarsacar.ecommercefirebase.ui.activities.SettingsActivity


class DashboardFragment : Fragment() {

  private var mBinding: FragmentDashboardBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val _binding get() = mBinding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // if we want to use the option menu in fragment we need to add it
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mBinding = FragmentDashboardBinding.inflate(inflater, container, false)

    _binding.textDashboard.text = "Dashboard Fragment"

    return _binding.root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

    inflater.inflate(R.menu.dashboard_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    when (id) {
      R.id.action_settings -> {
        startActivity(Intent(activity, SettingsActivity::class.java))
      }
    }

    return super.onOptionsItemSelected(item)
  }


  override fun onDestroyView() {
    super.onDestroyView()
    mBinding = null
  }
}