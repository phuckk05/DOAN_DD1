package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.da.R
import com.example.da.database.DatabaseHelper

class RegisterFragment : Fragment() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        dbHelper = DatabaseHelper(requireContext())

        etUsername = view.findViewById(R.id.etUsername)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnRegister = view.findViewById(R.id.btnRegister)
        tvGoToLogin = view.findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener { handleRegister() }

        tvGoToLogin.setOnClickListener {
            parentFragmentManager.popBackStack() // Quay lại LoginFragment
        }

        return view
    }

    private fun handleRegister() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.isUserExists(username)) {
            Toast.makeText(requireContext(), "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo tài khoản admin đầu tiên
        val role = if (username.equals("admin", ignoreCase = true)) "admin" else "user"
        val userId = dbHelper.addUser(username, password, role)

        if (userId != -1L) {
            Toast.makeText(requireContext(), "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack() // Quay lại màn hình đăng nhập
        } else {
            Toast.makeText(requireContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
        }
    }
}
