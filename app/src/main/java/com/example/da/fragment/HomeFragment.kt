package com.example.da.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.SessionManager
import com.example.da.activity.AuthActivity
import com.example.da.adapter.TestAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Test
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    // Khai báo các View
    private lateinit var rvTestList: RecyclerView
    private lateinit var btnCreateTest: MaterialButton
    private lateinit var btnHistory: Button
    private lateinit var btnLogout: Button

    // Khai báo các đối tượng cần thiết
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var testAdapter: TestAdapter
    private val testList = mutableListOf<Test>()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo các đối tượng
        dbHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        // Ánh xạ các view
        setControl(view)

        // Phân quyền hiển thị cho các nút
        setupPermissions()

        // Cài đặt sự kiện cho tất cả các nút
        setEvent()

        // Tải dữ liệu ban đầu
        setupRecyclerView()
        loadTests()
    }

    private fun setControl(view: View) {
        rvTestList = view.findViewById(R.id.rvTestList)
        btnCreateTest = view.findViewById(R.id.btnCreateTest)
        btnHistory = view.findViewById(R.id.btnHistory)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun setupPermissions() {
        // ==========================================================
        // ===== LOGIC PHÂN QUYỀN ĐÃ ĐƯỢC CẬP NHẬT (QUAN TRỌNG) =====
        // ==========================================================

        val isAdmin = sessionManager.getUserRole() == "admin"

        if (isAdmin) {
            // NẾU LÀ ADMIN:
            // 1. Hiện nút "Tạo đề thi"
            btnCreateTest.visibility = View.VISIBLE
            // 2. Ẩn nút "Đăng xuất" (vì đã có trong màn hình Quản lý)
            btnLogout.visibility = View.GONE
        } else {
            // NẾU LÀ USER:
            // 1. Ẩn nút "Tạo đề thi"
            btnCreateTest.visibility = View.GONE
            // 2. Hiện nút "Đăng xuất"
            btnLogout.visibility = View.VISIBLE
        }
    }

    private fun setEvent() {
        // --- SỰ KIỆN CHO CÁC NÚT MÀ AI CŨNG DÙNG ĐƯỢC ---
        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            sessionManager.logoutUser()
            val intent = Intent(activity, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        }

        // --- SỰ KIỆN CHO NÚT MÀ CHỈ ADMIN DÙNG ĐƯỢC ---
        btnCreateTest.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateTestFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTests()
    }

    private fun setupRecyclerView() {
        testAdapter = TestAdapter(testList) { test ->
            val fragment = TestHistoryFragment.newInstance(test.testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvTestList.layoutManager = LinearLayoutManager(requireContext())
        rvTestList.adapter = testAdapter
    }

    private fun loadTests() {
        val tests = dbHelper.getAllTests()
        testList.clear()
        testList.addAll(tests)
        if (::testAdapter.isInitialized) {
            testAdapter.notifyDataSetChanged()
        }
    }
}
