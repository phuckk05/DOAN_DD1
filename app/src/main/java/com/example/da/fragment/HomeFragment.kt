package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.TestAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Test
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var rvTestList: RecyclerView
    private lateinit var tvCreate: MaterialButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var testAdapter: TestAdapter
    private val testList = mutableListOf<Test>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        rvTestList = view.findViewById(R.id.rvTestList)
        tvCreate = view.findViewById(R.id.tvCreate)

        // Setup RecyclerView
        setupRecyclerView()
        loadTests()

        // Setup Create button click listener
        tvCreate.setOnClickListener {
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
        testAdapter.notifyDataSetChanged()
    }
}
