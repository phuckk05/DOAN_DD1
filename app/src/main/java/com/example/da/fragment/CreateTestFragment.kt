package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.da.R

class CreateTestFragment : Fragment() {

    private lateinit var ivBack: TextView
    private lateinit var tvAdd: TextView
    private lateinit var spinnerSubject: Spinner
    private lateinit var cbEasy: CheckBox
    private lateinit var cbMedium: CheckBox
    private lateinit var cbHard: CheckBox
    private lateinit var etNumberQuestions: EditText
    private lateinit var rgMultipleChoice: RadioGroup
    private lateinit var rbYes: RadioButton
    private lateinit var rbNo: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun setupSubjectSpinner() {
        val subjects = arrayOf("Tiếng Anh", "Toán", "Vật Lý", "Hóa Học", "Sinh Học", "Lịch Sử", "Địa Lý")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter
    }

    private fun createTest() {
        // Validate inputs
        if (!cbEasy.isChecked && !cbMedium.isChecked && !cbHard.isChecked) {
            Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một mức độ", Toast.LENGTH_SHORT).show()
            return
        }

        val numQuestions = etNumberQuestions.text.toString()
        if (numQuestions.isEmpty()) {
            etNumberQuestions.error = "Vui lòng nhập số câu hỏi"
            etNumberQuestions.requestFocus()
            return
        }

        val questionCount = numQuestions.toIntOrNull()
        if (questionCount == null || questionCount <= 0) {
            etNumberQuestions.error = "Số câu hỏi phải là số nguyên dương"
            etNumberQuestions.requestFocus()
            return
        }

        // TODO: Implement test creation logic
        val subject = spinnerSubject.selectedItem.toString()
        val difficulties = mutableListOf<String>()
        if (cbEasy.isChecked) difficulties.add("Dễ")
        if (cbMedium.isChecked) difficulties.add("Vừa")
        if (cbHard.isChecked) difficulties.add("Khó")
        val isMultipleChoice = rbYes.isChecked

        Toast.makeText(
            requireContext(),
            "Tạo đề thi thành công!\nMôn: $subject\nSố câu: $questionCount",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate back
        parentFragmentManager.popBackStack()
    }
}
