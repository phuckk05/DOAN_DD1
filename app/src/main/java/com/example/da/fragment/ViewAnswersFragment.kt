package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.AnswerAdapter
import com.example.da.database.DatabaseHelper

class ViewAnswersFragment : Fragment() {

    private var testId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var ivBack: ImageView
    private lateinit var tvTestTitle: TextView
    private lateinit var rvAnswers: RecyclerView

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
        return inflater.inflate(R.layout.fragment_view_answers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        setControl(view)
        setEvent()
    }

    // Initialize views
    private fun setControl(view: View) {
        ivBack = view.findViewById(R.id.ivBack)
        tvTestTitle = view.findViewById(R.id.tvTestTitle)
        rvAnswers = view.findViewById(R.id.rvAnswers)
    }

    // Setup event listeners
    private fun setEvent() {
        val test = dbHelper.getTestById(testId)
        tvTestTitle.text = test?.name ?: "Đáp án"

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val questions = dbHelper.getQuestionsForTest(dbHelper.getTestById(testId)!!)
        val adapter = AnswerAdapter(questions, dbHelper)
        rvAnswers.adapter = adapter
        rvAnswers.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        @JvmStatic
        fun newInstance(testId: Int) =
            ViewAnswersFragment().apply {
                arguments = Bundle().apply {
                    putInt("testId", testId)
                }
            }
    }
}