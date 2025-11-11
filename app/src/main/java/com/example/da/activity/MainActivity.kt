package com.example.da.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.da.R
import com.example.da.fragment.HomeFragment
import com.example.da.fragment.ManagementFragment
import com.example.da.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_management -> ManagementFragment()
                R.id.navigation_profile -> ProfileFragment()
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

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }
}