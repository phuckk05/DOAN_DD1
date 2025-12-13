package com.example.da.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.da.R
import com.example.da.fragment.HomeFragment
import com.example.da.fragment.ManagementFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setControl()
        setEvent()

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    // Initialize views
    private fun setControl() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    // Setup event listeners
    private fun setEvent() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_management -> ManagementFragment()
                else -> null
            }
            selectedFragment?.let {
                try {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, it).commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }
    }

    fun showBottomNavigation(show: Boolean) {
        // Ensure bottomNavigation is initialized if called from outside before onCreate (unlikely but safe)
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
        } else {
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}