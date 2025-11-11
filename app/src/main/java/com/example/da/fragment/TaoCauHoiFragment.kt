package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.da.R

class TaoCauHoiFragment : Fragment() {

    private lateinit var etQuestion: EditText
    private lateinit var spinnerSubject: Spinner
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var tvAddAnswer: TextView
    private lateinit var tvAdd: TextView
    private lateinit var ivBack: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tao_cau_hoi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        etQuestion = view.findViewById(R.id.etQuestion)
        spinnerSubject = view.findViewById(R.id.spinnerSubject)
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)
        tvAddAnswer = view.findViewById(R.id.tvAddAnswer)
        tvAdd = view.findViewById(R.id.tvAdd)
        ivBack = view.findViewById(R.id.ivBack)

        // Setup spinners
        setupSubjectSpinner()
        setupDifficultySpinner()

        // Setup click listeners
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvAdd.setOnClickListener {
            saveQuestion()
        }

        tvAddAnswer.setOnClickListener {
            addNewAnswerField()
        }
    }

    private fun setupSubjectSpinner() {
        val subjects = arrayOf("Tiếng Anh", "Toán", "Vật Lý", "Hóa Học", "Sinh Học", "Lịch Sử", "Địa Lý")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter
    }

    private fun setupDifficultySpinner() {
        val difficulties = arrayOf("Dễ", "Vừa", "Khó")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDifficulty.adapter = adapter
    }

    private fun addNewAnswerField() {
        // This would add a new answer field dynamically
        // Implementation depends on your data model
    }

    private fun saveQuestion() {
        val question = etQuestion.text.toString()
        val subject = spinnerSubject.selectedItem.toString()
        val difficulty = spinnerDifficulty.selectedItem.toString()

        if (question.isEmpty()) {
            etQuestion.error = "Vui lòng nhập câu hỏi"
            return
        }

        // Save to database or send to server
        // TODO: Implement save logic
    }
}

