package com.example.da.fragment

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question
import com.example.da.model.Test
import com.example.da.model.TestResult
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class DoTestFragment : Fragment() {

    private var testId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentTest: Test
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0

    private lateinit var tvTimer: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var rgAnswers: RadioGroup
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button

    private var timer: CountDownTimer? = null
    // store selections as set of answer IDs per question (works for single and multi choice)
    private val userAnswers = mutableMapOf<Int, MutableSet<Int>>() // Question ID -> Selected Answer IDs
    private var timeTakenSeconds: Int = 0


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
        return inflater.inflate(R.layout.fragment_do_test, container, false)
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

        tvTimer = view.findViewById(R.id.tvTimer)
        tvQuestionNumber = view.findViewById(R.id.tvQuestionNumber)
        tvQuestionText = view.findViewById(R.id.tvQuestionText)
        rgAnswers = view.findViewById(R.id.rgAnswers)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnNext = view.findViewById(R.id.btnNext)

        // Dummy data for now
        if (::currentTest.isInitialized) {
            startTimer(currentTest.durationMinutes.toLong())
            displayQuestion()
        } else {
            Toast.makeText(context, "Không tìm thấy bài test", Toast.LENGTH_SHORT).show()
        }


        btnNext.setOnClickListener {
            saveUserAnswer()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                finishTest()
            }
        }

        btnPrevious.setOnClickListener {
            saveUserAnswer()
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion()
            }
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

    private fun startTimer(minutes: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(minutes * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                tvTimer.text = hms
                timeTakenSeconds = (minutes * 60 - TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)).toInt()
            }

            override fun onFinish() {
                tvTimer.text = "Hết giờ!"
                finishTest()
            }
        }.start()
    }

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]
        tvQuestionNumber.text = "Câu hỏi ${currentQuestionIndex + 1}/${questions.size}"
        tvQuestionText.text = question.text
        rgAnswers.removeAllViews()

        val answers = dbHelper.getAnswersByQuestion(question.id)

        // get previously selected set or empty set
        val selectedSet = userAnswers.getOrPut(question.id) { mutableSetOf() }

        if (question.isMultipleChoice) {
            // render checkboxes
            answers.forEach { answer ->
                val checkBox = CheckBox(context).apply {
                    text = answer.answerText
                    id = answer.id
                    isChecked = selectedSet.contains(answer.id)
                    setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                        val set = userAnswers.getOrPut(question.id) { mutableSetOf() }
                        if (isChecked) set.add(answer.id) else set.remove(answer.id)
                    }
                }
                rgAnswers.addView(checkBox)
            }
        } else {
            // render radio buttons
            answers.forEach { answer ->
                val radioButton = RadioButton(context).apply {
                    text = answer.answerText
                    id = answer.id
                }
                rgAnswers.addView(radioButton)
            }

            // Restore selected answer (single choice)
            if (selectedSet.isNotEmpty()) {
                rgAnswers.check(selectedSet.first())
            }
        }

        updateButtonStates()
    }

    private fun saveUserAnswer() {
        val question = questions[currentQuestionIndex]
        if (question.isMultipleChoice) {
            // collect checked checkboxes
            val selected = mutableSetOf<Int>()
            for (i in 0 until rgAnswers.childCount) {
                val v = rgAnswers.getChildAt(i)
                if (v is CheckBox && v.isChecked) selected.add(v.id)
            }
            if (selected.isNotEmpty()) userAnswers[question.id] = selected else userAnswers.remove(question.id)
        } else {
            val selectedId = rgAnswers.checkedRadioButtonId
            if (selectedId != -1) {
                userAnswers[questions[currentQuestionIndex].id] = mutableSetOf(selectedId)
            } else {
                userAnswers.remove(questions[currentQuestionIndex].id)
            }
        }
    }

    private fun finishTest() {
        timer?.cancel()
        var correctAnswers = 0
        for (question in questions) {
            val correctAnswerIds = dbHelper.getAnswersByQuestion(question.id).filter { it.isCorrect }.map { it.id }.toSet()
            val userSelected = userAnswers[question.id] ?: emptySet()

            if (question.isMultipleChoice) {
                // require exact match for full credit
                if (userSelected == correctAnswerIds && userSelected.isNotEmpty()) correctAnswers++
            } else {
                if (userSelected.size == 1 && correctAnswerIds.contains(userSelected.first())) correctAnswers++
            }
        }

        // Calculate score on a 10-point scale and round to nearest integer
        val score = (correctAnswers.toDouble() / questions.size * 10).roundToInt()

        val testResult = TestResult(
            testId = testId,
            score = score,
            timeTakenSeconds = timeTakenSeconds,
            timestamp = System.currentTimeMillis()
        )

        dbHelper.addTestResult(testResult)

        Toast.makeText(context, "Bạn đã đạt: $score/10 điểm", Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack()
    }

    private fun updateButtonStates() {
        btnPrevious.isEnabled = currentQuestionIndex > 0
        btnNext.text = if (currentQuestionIndex == questions.size - 1) "Nộp bài" else "Câu tiếp"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance(testId: Int) =
            DoTestFragment().apply {
                arguments = Bundle().apply {
                    putInt("testId", testId)
                }
            }
    }
}
