package com.example.da.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.da.R

class MainResultFragment : Fragment() {

    companion object {
        fun newInstance(score: Int, total: Int) = MainResultFragment().apply {
            arguments = Bundle().apply {
                putInt("score", score)
                putInt("total", total)
            }
        }
    }

    private var score = 0
    private var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getInt("score")
            total = it.getInt("total")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtScore = view.findViewById<TextView>(R.id.txtScore)
        val btnViewAnswer = view.findViewById<Button>(R.id.btnViewAnswer)
        val btnMenu = view.findViewById<Button>(R.id.btnMenu)
        val btnRetry = view.findViewById<Button>(R.id.btnRetry)

        txtScore.text = "Điểm của bạn: $score\nSố câu đúng: $score/$total"

        btnMenu.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        btnRetry.setOnClickListener {
            val testFragment = MainDoTestFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, testFragment)
                .commit()
        }

        btnViewAnswer.setOnClickListener {
            // Chuyển sang MainAnswerFragment nếu muốn xem đáp án
            Toast.makeText(requireContext(), "Chức năng xem đáp án chưa làm", Toast.LENGTH_SHORT).show()
        }

        btnViewAnswer.setOnClickListener {
            // Chuyển sang MainAnswerFragment với danh sách đáp án người dùng
            val userAnswers = listOf("2", "5", "11") // Lấy từ MainDoTestFragment
            val answerFragment = MainAnswerFragment.newInstance(userAnswers)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, answerFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
