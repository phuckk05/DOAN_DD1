package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.adapter.TestHistoryAdapter
import com.example.da.database.DatabaseHelper

class TestHistoryFragment : Fragment() {

    private var testId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            testId = it.getInt("testId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        val tvTestTitle = view.findViewById<TextView>(R.id.tvTestTitle)
        val rvTestHistory = view.findViewById<RecyclerView>(R.id.rvTestHistory)
        val btnStartTest = view.findViewById<Button>(R.id.btnStartTest)

        val test = dbHelper.getTestById(testId)
        tvTestTitle.text = test?.name ?: "Lịch sử làm bài"

        val testResults = dbHelper.getTestResults(testId)
        val adapter = TestHistoryAdapter(testResults) { testId ->
            val viewAnswersFragment = ViewAnswersFragment.newInstance(testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewAnswersFragment)
                .addToBackStack(null)
                .commit()
        }
        rvTestHistory.adapter = adapter
        rvTestHistory.layoutManager = LinearLayoutManager(context)

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnStartTest.setOnClickListener {
            val doTestFragment = DoTestFragment.newInstance(testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, doTestFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNavigation(false)
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation(true)
    }

    companion object {
        @JvmStatic
        fun newInstance(testId: Int) =
            TestHistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt("testId", testId)
                }
            }
    }
}
