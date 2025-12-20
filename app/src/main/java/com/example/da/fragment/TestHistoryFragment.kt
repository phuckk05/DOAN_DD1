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

    // Khai báo các View
    private lateinit var ivBack: ImageView
    private lateinit var tvTestTitle: TextView
    private lateinit var rvTestHistory: RecyclerView
    private lateinit var btnStartTest: Button
    private lateinit var btnViewAllHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lấy testId từ arguments
        arguments?.let {
            testId = it.getInt("testId", -1)
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

        setControl(view)
        setEvent()
    }

    // Ánh xạ các View từ layout
    private fun setControl(view: View) {
        ivBack = view.findViewById(R.id.ivBack)
        tvTestTitle = view.findViewById(R.id.tvTestTitle)
        rvTestHistory = view.findViewById(R.id.rvTestHistory)
        btnStartTest = view.findViewById(R.id.btnStartTest)
        btnViewAllHistory = view.findViewById(R.id.btnViewAllHistory)
    }

    // Cài đặt các trình xử lý sự kiện
    private fun setEvent() {
        // Lấy thông tin đề thi và hiển thị tiêu đề
        val test = dbHelper.getTestById(testId)
        tvTestTitle.text = test?.name ?: "Lịch sử làm bài"

        // Lấy kết quả thi của đề này và hiển thị lên RecyclerView
        val testResults = dbHelper.getTestResults(testId)
        val adapter = TestHistoryAdapter(testResults) { result ->
            // =======================================================
            //  SỬA LỖI Ở ĐÂY: ĐỔI `result.resultId` THÀNH `result.id`
            // =======================================================
            val viewAnswersFragment = ViewAnswersFragment.newInstance(result.id) // <-- ĐÃ SỬA

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewAnswersFragment)
                .addToBackStack(null)
                .commit()
        }
        rvTestHistory.adapter = adapter
        rvTestHistory.layoutManager = LinearLayoutManager(context)

        // Sự kiện cho nút quay lại
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Sự kiện cho nút Làm bài
        btnStartTest.setOnClickListener {
            val doTestFragment = DoTestFragment.newInstance(testId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, doTestFragment)
                .addToBackStack(null)
                .commit()
        }

        // Sự kiện cho nút "TẤT CẢ LỊCH SỬ"
        btnViewAllHistory.setOnClickListener {
            // Chuyển sang màn hình HistoryFragment để xem toàn bộ lịch sử
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .addToBackStack(null) // Cho phép người dùng nhấn back để quay lại
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Ẩn thanh điều hướng dưới cùng khi vào màn hình này
        (activity as? MainActivity)?.showBottomNavigation(false)
    }

    override fun onPause() {
        super.onPause()
        // Hiện lại thanh điều hướng dưới cùng khi rời khỏi màn hình này
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
