package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.AnswerAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question

class ViewAnswersFragment : Fragment() {

    private lateinit var recyclerViewAnswers: RecyclerView
    private lateinit var answerAdapter: AnswerAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var testId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            testId = it.getInt("TEST_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_answers, container, false)
        dbHelper = DatabaseHelper(requireContext())

        val tvTestTitle = view.findViewById<TextView>(R.id.tvTestTitle)
        recyclerViewAnswers = view.findViewById(R.id.recyclerViewAnswers)
        recyclerViewAnswers.layoutManager = LinearLayoutManager(requireContext())

        val test = dbHelper.getTestById(testId)
        if (test != null) {
            tvTestTitle.text = test.name
            val questions = dbHelper.getQuestionsForTest(test)
            answerAdapter = AnswerAdapter(questions, dbHelper)
            recyclerViewAnswers.adapter = answerAdapter
        } else {
            tvTestTitle.text = "Test Not Found"
            Toast.makeText(requireContext(), "Test not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(testId: Int) =
            ViewAnswersFragment().apply {
                arguments = Bundle().apply {
                    putInt("TEST_ID", testId)
                }
            }
    }
}