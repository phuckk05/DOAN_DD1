package com.example.da.fragment

import android.content.Intent
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
import com.example.da.activity.MainActivity
import com.example.da.database.DatabaseHelper
import com.example.da.SessionManager

class LoginFragment : Fragment() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        dbHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        etUsername = view.findViewById(R.id.etUsername)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvGoToRegister = view.findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener { handleLogin() }

        tvGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.auth_fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun handleLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val userResult = dbHelper.checkUser(username, password)
        if (userResult != null) {
            val (role, userId) = userResult
            sessionManager.createLoginSession(userId, username, role)

            Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

            // Chuyển sang MainActivity và xóa hết các màn hình trước đó
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(requireContext(), "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
        }
    }
}
