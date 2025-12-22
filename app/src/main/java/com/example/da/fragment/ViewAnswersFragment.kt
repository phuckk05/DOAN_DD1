package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.AnswerAdapter // Vẫn dùng adapter cũ của bạn
import com.example.da.database.DatabaseHelper

class ViewAnswersFragment : Fragment() {

    private var resultId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var ivBack: ImageView
    private lateinit var tvTestTitle: TextView
    private lateinit var rvAnswers: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Nhận resultId từ màn hình trước
            resultId = it.getInt(ARG_RESULT_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_answers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        setControl(view)
        setEvent()
    }

    private fun setControl(view: View) {
        ivBack = view.findViewById(R.id.ivBack)
        tvTestTitle = view.findViewById(R.id.tvTestTitle)
        rvAnswers = view.findViewById(R.id.rvAnswers)
    }

    private fun setEvent() {
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        if (resultId == -1) {
            Toast.makeText(context, "Lỗi: Không tìm thấy ID của lần làm bài.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val testResult = dbHelper.getTestResultById(resultId)
        if (testResult == null) {
            Toast.makeText(context, "Lỗi: Không tìm thấy dữ liệu của lần làm bài này.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val test = dbHelper.getTestById(testResult.testId)
        if (test == null) {
            Toast.makeText(context, "Không tìm thấy thông tin đề thi được liên kết.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        tvTestTitle.text = "Đáp án: ${test.name}"


        val questions = dbHelper.getQuestionsForTest(test)
        if (questions.isEmpty()) {
            Toast.makeText(context, "Đề thi này không có câu hỏi.", Toast.LENGTH_SHORT).show()
            return
        }


        val adapter = AnswerAdapter(questions, dbHelper)
        rvAnswers.adapter = adapter
        rvAnswers.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        private const val ARG_RESULT_ID = "result_id"

        @JvmStatic
        fun newInstance(resultId: Int) =
            ViewAnswersFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RESULT_ID, resultId)
                }
            }
    }
}
