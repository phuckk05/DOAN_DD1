package com.example.da.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.da.R
import com.example.da.model.Question
import com.example.da.model.QuestionData

class MainDoTestFragment : Fragment() {

    private lateinit var tvQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button
    private lateinit var tvTimer: TextView

    private var currentIndex = 0
    // 3 câu hỏi mẫu chắc chắn có
    private val questionList = listOf(
        Question(1, "Toán", "1 + 1 = ?", "Dễ"),
        Question(2, "Toán", "2 + 3 = ?", "Dễ"),
        Question(3, "Toán", "5 + 6 = ?", "Dễ")
    )

    private val options = listOf(
        listOf("1","2","3","4"),
        listOf("4","5","6","7"),
        listOf("10","11","12","13")
    )

    private val correctAnswers = listOf(1, 1, 1)

    private val userAnswer = MutableList(questionList.size) { -1 }

    private lateinit var timer: CountDownTimer
    private val totalTimeMillis: Long = 5 * 60 * 1000 // 5 phút

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_do_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvQuestion = view.findViewById(R.id.txtQuestion)
        radioGroup = view.findViewById(R.id.radioGroup)
        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        tvTimer = view.findViewById(R.id.tvTimer)

        loadQuestion()
        startTimer()

        btnNext.setOnClickListener {
            saveAnswer()
            if (currentIndex < questionList.size - 1) {
                currentIndex++
                loadQuestion()
            }
        }

        btnBack.setOnClickListener {
            saveAnswer()
            if (currentIndex > 0) {
                currentIndex--
                loadQuestion()
            }
        }

        btnSubmit.setOnClickListener {
            submitTest()
        }
    }

    private fun loadQuestion() {
        val q = questionList[currentIndex]
        tvQuestion.text = "Câu ${currentIndex + 1}: ${q.text}"

        radioGroup.removeAllViews()
        options[currentIndex].forEachIndexed { index, text ->
            val rb = RadioButton(requireContext())
            rb.text = text
            rb.id = index
            radioGroup.addView(rb)
        }

        if (userAnswer[currentIndex] != -1) {
            radioGroup.check(userAnswer[currentIndex])
        }

        btnBack.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < questionList.size - 1
    }

    private fun saveAnswer() {
        val checkedId = radioGroup.checkedRadioButtonId
        if (checkedId != -1) userAnswer[currentIndex] = checkedId
    }

    private fun submitTest() {
        saveAnswer()
        val score = userAnswer.indices.count { userAnswer[it] == correctAnswers[it] }

        if (::timer.isInitialized) timer.cancel()

        val resultFragment = MainResultFragment.newInstance(score, questionList.size)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, resultFragment)
            .commit()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(totalTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tvTimer.text = String.format("Thời gian: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                Toast.makeText(requireContext(), "Hết thời gian!", Toast.LENGTH_SHORT).show()
                submitTest()
            }
        }
        timer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::timer.isInitialized) timer.cancel()
    }
}
