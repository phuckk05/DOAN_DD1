package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.SessionManager
import com.example.da.activity.MainActivity
import com.example.da.adapter.TestHistoryAdapter
import com.example.da.database.DatabaseHelper


class TestHistoryFragment : Fragment() {

    private var testId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    // Khai báo các View
    private lateinit var ivBack: ImageView
    private lateinit var tvTestTitle: TextView
    private lateinit var rvTestHistory: RecyclerView
    private lateinit var btnStartTest: Button
    private lateinit var btnViewAllHistory: Button
    private lateinit var btnPracticeTest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            testId = it.getInt("testId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Khởi tạo các helper
        dbHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        // Gán view và thiết lập sự kiện
        setControl(view)
        setEvent()
    }

    private fun setControl(view: View) {
        ivBack = view.findViewById(R.id.ivBack)
        tvTestTitle = view.findViewById(R.id.tvTestTitle)
        rvTestHistory = view.findViewById(R.id.rvTestHistory)
        btnStartTest = view.findViewById(R.id.btnStartTest)
        btnViewAllHistory = view.findViewById(R.id.btnViewAllHistory)
        btnPracticeTest = view.findViewById(R.id.btnPracticeTest)
    }

    private fun setEvent() {
        if (testId == -1) {
            Toast.makeText(context, "Lỗi: ID đề thi không hợp lệ.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val test = dbHelper.getTestById(testId)
        if (test == null) {
            Toast.makeText(context, "Lỗi: Không tìm thấy thông tin đề thi.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        tvTestTitle.text = test.name


            btnStartTest.visibility = View.VISIBLE
            btnPracticeTest.visibility = View.VISIBLE


        val testResults = dbHelper.getTestResults(testId)
        val adapter = TestHistoryAdapter(testResults) { result ->
            val viewAnswersFragment = ViewAnswersFragment.newInstance(result.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewAnswersFragment)
                .addToBackStack(null)
                .commit()
        }
        rvTestHistory.adapter = adapter
        rvTestHistory.layoutManager = LinearLayoutManager(context)

        // 6. Thiết lập sự kiện cho các nút
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnPracticeTest.setOnClickListener {
            val practiceTestFragment = PracticeTestFragment.newInstance(testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, practiceTestFragment)
                .addToBackStack(null)
                .commit()
        }

        btnStartTest.setOnClickListener {
            val doTestFragment = DoTestFragment.newInstance(testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, doTestFragment)
                .addToBackStack(null)
                .commit()
        }

        btnViewAllHistory.setOnClickListener {
            // Chuyển đến màn hình lịch sử chung
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // --- Quản lý vòng đời Fragment ---

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
