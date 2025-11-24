package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.database.DatabaseHelper
import com.example.da.model.Subject
import com.example.da.model.Test

class CreateTestFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var tvAdd: TextView
    private lateinit var spinnerSubject: Spinner
    private lateinit var cbEasy: CheckBox
    private lateinit var cbMedium: CheckBox
    private lateinit var cbHard: CheckBox
    private lateinit var etNumberQuestions: EditText
    private lateinit var rgMultipleChoice: RadioGroup
    private lateinit var rbYes: RadioButton
    private lateinit var rbNo: RadioButton
    private lateinit var etMinutes: EditText
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var subjects: List<Subject>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // Initialize views
        ivBack = view.findViewById(R.id.ivBack)
        tvAdd = view.findViewById(R.id.tvAdd)
        spinnerSubject = view.findViewById(R.id.spinner_subject)
        cbEasy = view.findViewById(R.id.cb_easy)
        cbMedium = view.findViewById(R.id.cb_medium)
        cbHard = view.findViewById(R.id.cb_hard)
        etNumberQuestions = view.findViewById(R.id.et_number_questions)
        rgMultipleChoice = view.findViewById(R.id.rg_multiple_choice)
        rbYes = view.findViewById(R.id.rb_yes)
        rbNo = view.findViewById(R.id.rb_no)
        etMinutes = view.findViewById(R.id.et_minutes)

        // Setup subject spinner
        setupSubjectSpinner()

        // Setup click listeners
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvAdd.setOnClickListener {
            createTest()
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

    private fun setupSubjectSpinner() {
        subjects = dbHelper.getAllSubjects()
        val subjectNames = subjects.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter
    }

    private fun createTest() {
        // Validate inputs
        val selectedSubjectPosition = spinnerSubject.selectedItemPosition
        if (selectedSubjectPosition < 0 || selectedSubjectPosition >= subjects.size) {
            Toast.makeText(requireContext(), "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedSubject = subjects[selectedSubjectPosition]


        val checkedDifficulties = listOf(cbEasy, cbMedium, cbHard).filter { it.isChecked }
        if (checkedDifficulties.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một mức độ", Toast.LENGTH_SHORT).show()
            return
        }

        val numQuestionsStr = etNumberQuestions.text.toString()
        if (numQuestionsStr.isEmpty()) {
            etNumberQuestions.error = "Vui lòng nhập số câu hỏi"
            etNumberQuestions.requestFocus()
            return
        }

        val questionCount = numQuestionsStr.toIntOrNull()
        if (questionCount == null || questionCount <= 0) {
            etNumberQuestions.error = "Số câu hỏi phải là số nguyên dương"
            etNumberQuestions.requestFocus()
            return
        }

        val minutesStr = etMinutes.text.toString()
        if (minutesStr.isEmpty()) {
            etMinutes.error = "Vui lòng nhập thời gian"
            etMinutes.requestFocus()
            return
        }

        val minutes = minutesStr.toIntOrNull()
        if (minutes == null || minutes <= 0) {
            etMinutes.error = "Thời gian phải là số nguyên dương"
            etMinutes.requestFocus()
            return
        }

        val allowMultipleAnswers = rbYes.isChecked

        // Calculate difficulty percentages
        val totalChecked = checkedDifficulties.size
        val percentPerSelection = 100 / totalChecked
        var easyPercent = 0
        var mediumPercent = 0
        var hardPercent = 0

        checkedDifficulties.forEach {
            when (it.id) {
                R.id.cb_easy -> easyPercent = percentPerSelection
                R.id.cb_medium -> mediumPercent = percentPerSelection
                R.id.cb_hard -> hardPercent = percentPerSelection
            }
        }

        // Adjust for rounding errors, give remainder to the first checked box
        val remainder = 100 % totalChecked
        if (remainder > 0 && checkedDifficulties.isNotEmpty()) {
            when (checkedDifficulties.first().id) {
                R.id.cb_easy -> easyPercent += remainder
                R.id.cb_medium -> mediumPercent += remainder
                R.id.cb_hard -> hardPercent += remainder
            }
        }


        val testName = "Đề thi môn ${selectedSubject.name}"

        val newTest = Test(
            subjectId = selectedSubject.id,
            name = testName,
            numQuestions = questionCount,
            durationMinutes = minutes,
            allowMultipleAnswers = allowMultipleAnswers,
            easyPercent = easyPercent,
            mediumPercent = mediumPercent,
            hardPercent = hardPercent
        )

        val result = dbHelper.addTest(newTest)

        if (result != -1L) {
            Toast.makeText(
                requireContext(),
                "Tạo đề thi thành công!",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "Lỗi khi tạo đề thi",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
