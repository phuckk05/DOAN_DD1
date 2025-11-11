package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.TestAdapter
import com.example.da.adapter.TestItem

class HomeFragment : Fragment() {

    private lateinit var rvTestList: RecyclerView
    private lateinit var tvCreate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTestList = view.findViewById(R.id.rvTestList)
        tvCreate = view.findViewById(R.id.tvCreate)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup Create button click listener
        tvCreate.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateTestFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        // Sample data
        val testList = listOf(
            TestItem(1, "Toán", "Đề", "60 Phút", R.drawable.ic_home_24),
            TestItem(2, "Lý", "Đề", "45 Phút", R.drawable.ic_home_24),
            TestItem(3, "Hóa", "Đề", "15 Phút", R.drawable.ic_home_24),
            TestItem(4, "Tiếng anh", "Đề", "60 Phút", R.drawable.ic_home_24)
        )

        val adapter = TestAdapter(testList)
        rvTestList.layoutManager = LinearLayoutManager(requireContext())
        rvTestList.adapter = adapter
    }
}
