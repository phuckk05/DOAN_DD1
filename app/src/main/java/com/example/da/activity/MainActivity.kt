package com.example.da.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.da.R
import com.example.da.SessionManager
import com.example.da.fragment.HistoryFragment // <-- THÊM IMPORT NÀY
import com.example.da.fragment.HomeFragment
import com.example.da.fragment.ManagementFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo SessionManager
        sessionManager = SessionManager(this)

        // Lớp bảo vệ: Nếu chưa đăng nhập, quay về màn hình Auth
        if (!sessionManager.isLoggedIn()) {
            goToAuthActivity()
            return // Dừng hàm onCreate ở đây để không chạy code bên dưới
        }

        setControl()

        // ===== LOGIC PHÂN QUYỀN CHO THANH ĐIỀU HƯỚNG =====
        if (sessionManager.getUserRole() == "user") {
            // Nếu là USER, ẩn tab Quản lý đi
            bottomNavigation.menu.findItem(R.id.navigation_management).isVisible = false
        }
        // Nếu là "admin", không cần làm gì cả, tab đã hiển thị theo mặc định.

        setEvent()


        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        if (navigateTo == "HISTORY_FRAGMENT") {
            // Nếu có yêu cầu mở lịch sử, thì mở HistoryFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .commit()
        } else {
            // Nếu không có yêu cầu đặc biệt, mở HomeFragment như mặc định
            if (savedInstanceState == null) {
                bottomNavigation.selectedItemId = R.id.navigation_home
            }
        }
    }

    private fun setControl() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setEvent() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.navigation_home -> {
                    HomeFragment()
                }
                R.id.navigation_management -> {
                    // Chỉ admin mới thấy nút này, nhưng vẫn xử lý logic
                    ManagementFragment()
                }
                else -> null
            }

            // Dùng let để đảm bảo selectedFragment không null
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true // Listener cần trả về true để xác nhận item đã được chọn
        }
    }

    // Hàm tiện ích để quay về màn hình đăng nhập
    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    // Hàm để ẩn/hiện thanh điều hướng dưới cùng
    fun showBottomNavigation(show: Boolean) {
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}
