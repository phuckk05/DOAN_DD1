package com.example.da.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question
import com.example.da.model.Test

class PracticeTestFragment : Fragment() {

    private var testId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentTest: Test
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0

    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var rgAnswers: RadioGroup
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnShowAnswer: Button

    // store selections as set of answer IDs per question
    private val userAnswers = mutableMapOf<Int, MutableSet<Int>>()

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
        return inflater.inflate(R.layout.fragment_practice_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        val test = dbHelper.getTestById(testId)
        if (test == null) {
            Toast.makeText(context, "Không tìm thấy bài test", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }
        currentTest = test
        questions = dbHelper.getQuestionsForTest(currentTest)

        setControl(view)
        setEvent()
    }

    private fun setControl(view: View) {
        tvQuestionNumber = view.findViewById(R.id.tvQuestionNumber)
        tvQuestionText = view.findViewById(R.id.tvQuestionText)
        rgAnswers = view.findViewById(R.id.rgAnswers)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnNext = view.findViewById(R.id.btnNext)
        btnShowAnswer = view.findViewById(R.id.btnShowAnswer)
    }

    private fun setEvent() {
        if (::currentTest.isInitialized) {
            displayQuestion()
        }

        btnNext.setOnClickListener {
            saveUserAnswer()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                finishPractice()
            }
        }

        btnPrevious.setOnClickListener {
            saveUserAnswer()
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion()
            }
        }

        btnShowAnswer.setOnClickListener {
            showCorrectAnswer()
        }

        view?.findViewById<View>(R.id.ivBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
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

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]
        tvQuestionNumber.text = "Câu hỏi ${currentQuestionIndex + 1}/${questions.size}"
        tvQuestionText.text = question.text
        rgAnswers.removeAllViews()

        val answers = dbHelper.getAnswersByQuestion(question.id)
        val selectedSet = userAnswers.getOrPut(question.id) { mutableSetOf() }

        if (question.isMultipleChoice) {
            answers.forEach { answer ->
                val checkBox = CheckBox(context).apply {
                    text = answer.answerText
                    id = answer.id
                    isChecked = selectedSet.contains(answer.id)
                    setOnCheckedChangeListener { _, isChecked ->
                        val set = userAnswers.getOrPut(question.id) { mutableSetOf() }
                        if (isChecked) set.add(answer.id) else set.remove(answer.id)
                    }
                }
                rgAnswers.addView(checkBox)
            }
        } else {
            answers.forEach { answer ->
                val radioButton = RadioButton(context).apply {
                    text = answer.answerText
                    id = answer.id
                }
                rgAnswers.addView(radioButton)
            }
            if (selectedSet.isNotEmpty()) {
                rgAnswers.check(selectedSet.first())
            }
        }

        updateButtonStates()
    }

    private fun saveUserAnswer() {
        val question = questions[currentQuestionIndex]
        if (question.isMultipleChoice) {
            val selected = mutableSetOf<Int>()
            for (i in 0 until rgAnswers.childCount) {
                val v = rgAnswers.getChildAt(i)
                if (v is CheckBox && v.isChecked) selected.add(v.id)
            }
            if (selected.isNotEmpty()) userAnswers[question.id] = selected else userAnswers.remove(question.id)
        } else {
            val selectedId = rgAnswers.checkedRadioButtonId
            if (selectedId != -1) {
                userAnswers[question.id] = mutableSetOf(selectedId)
            } else {
                userAnswers.remove(question.id)
            }
        }
    }

    private fun showCorrectAnswer() {
        val question = questions[currentQuestionIndex]
        val answers = dbHelper.getAnswersByQuestion(question.id)

        for (i in 0 until rgAnswers.childCount) {
            val view = rgAnswers.getChildAt(i)
            val answerId = view.id
            val isCorrect = answers.find { it.id == answerId }?.isCorrect == true

            if (isCorrect) {
                if (view is TextView) {
                    view.setTextColor(Color.parseColor("#4CAF50"))
                    view.setTypeface(null, Typeface.BOLD)
                }
            }
        }
    }

    private fun finishPractice() {
        var correctAnswers = 0
        for (question in questions) {
            val correctAnswerIds = dbHelper.getAnswersByQuestion(question.id).filter { it.isCorrect }.map { it.id }.toSet()
            val userSelected = userAnswers[question.id] ?: emptySet()

            if (question.isMultipleChoice) {
                if (userSelected == correctAnswerIds && userSelected.isNotEmpty()) correctAnswers++
            } else {
                if (userSelected.size == 1 && correctAnswerIds.contains(userSelected.first())) correctAnswers++
            }
        }

        Toast.makeText(context, "Kết thúc thi thử. Bạn đúng $correctAnswers/${questions.size} câu.", Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack()
    }

    private fun updateButtonStates() {
        btnPrevious.isEnabled = currentQuestionIndex > 0
        btnNext.text = if (currentQuestionIndex == questions.size - 1) "Kết thúc" else "Câu tiếp"
    }

    companion object {
        @JvmStatic
        fun newInstance(testId: Int) =
            PracticeTestFragment().apply {
                arguments = Bundle().apply {
                    putInt("testId", testId)
                }
            }
    }
}