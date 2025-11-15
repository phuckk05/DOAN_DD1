
package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.da.R
import com.example.da.database.DatabaseHelper
import com.example.da.model.Subject

class TaoCauHoiFragment : Fragment() {

    private lateinit var etQuestion: EditText
    private lateinit var spinnerSubject: Spinner
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var tvAddAnswer: TextView
    private lateinit var tvAdd: TextView
    private lateinit var ivBack: TextView
    private lateinit var dbHelper: DatabaseHelper
    private var subjectsList = mutableListOf<Subject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tao_cau_hoi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        dbHelper = DatabaseHelper(requireContext())

        // Add sample subjects if database is empty (first run)
//        dbHelper.addSampleSubjects()

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

    override fun onResume() {
        super.onResume()
        // Refresh subjects list when fragment resumes (e.g., after adding a new subject)
        setupSubjectSpinner()
    }

    private fun setupSubjectSpinner() {
        // Load subjects from database
        subjectsList.clear()
        subjectsList.addAll(dbHelper.getAllSubjects())

        if (subjectsList.isEmpty()) {
            // Show message if no subjects available
            Toast.makeText(
                requireContext(),
                "Chưa có môn học nào. Vui lòng thêm môn học trước!",
                Toast.LENGTH_LONG
            ).show()

            // Add a default message
            subjectsList.add(Subject(id = 0, name = "Chưa có môn học"))
        }

        // Create adapter with subject names
        val subjectNames = subjectsList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
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
        val question = etQuestion.text.toString().trim()
        val difficulty = spinnerDifficulty.selectedItem.toString()

        // Validation
        if (question.isEmpty()) {
            etQuestion.error = "Vui lòng nhập câu hỏi"
            etQuestion.requestFocus()
            return
        }

        if (subjectsList.isEmpty() || subjectsList[0].id == 0) {
            Toast.makeText(
                requireContext(),
                "Vui lòng thêm môn học trước khi tạo câu hỏi!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedSubject = subjectsList[spinnerSubject.selectedItemPosition]

        // Save to database or send to server
        Toast.makeText(
            requireContext(),
            "Lưu câu hỏi cho môn '${selectedSubject.name}' thành công!",
            Toast.LENGTH_SHORT
        ).show()

        // Clear input
        etQuestion.text.clear()

        // TODO: Implement complete save logic with answers
    }
}

