package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.da.R

class CreateTestFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_test, container, false)

        // Xử lý nút back
        val ivBack = view.findViewById<TextView>(R.id.ivBack)
        ivBack.setOnClickListener {
            // Quay lại fragment trước đó
            parentFragmentManager.popBackStack()
        }

        return view
    }
}

