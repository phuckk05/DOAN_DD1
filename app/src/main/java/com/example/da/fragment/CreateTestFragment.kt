package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.adapter.TestAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Subject
import com.example.da.model.Test

class CreateTestFragment : Fragment() {

    private lateinit var ivBack: ImageView
    private lateinit var btnAdd: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tvMode: TextView
    private lateinit var etTestName: EditText
    private lateinit var spinnerSubject: Spinner
    private lateinit var cbEasy: CheckBox
    private lateinit var cbMedium: CheckBox
    private lateinit var cbHard: CheckBox
    private lateinit var etNumberQuestions: EditText
    private lateinit var rgMultipleChoice: RadioGroup
    private lateinit var rbYes: RadioButton
    private lateinit var rbNo: RadioButton
    private lateinit var etMinutes: EditText
    private lateinit var rvTests: RecyclerView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var subjects: List<Subject>
    private var tests: MutableList<Test> = mutableListOf()
    private var adapter: TestAdapter? = null
    private var currentEditingTest: Test? = null

    // selection modes
    private var selectionMode: SelectionMode = SelectionMode.NONE

    private enum class SelectionMode { NONE, EDIT, DELETE }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // Init views
        ivBack = view.findViewById(R.id.ivBack)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnDelete = view.findViewById(R.id.btnDelete)
        tvMode = view.findViewById(R.id.tvMode)
        etTestName = view.findViewById(R.id.et_test_name)
        spinnerSubject = view.findViewById(R.id.spinner_subject)
        cbEasy = view.findViewById(R.id.cb_easy)
        cbMedium = view.findViewById(R.id.cb_medium)
        cbHard = view.findViewById(R.id.cb_hard)
        etNumberQuestions = view.findViewById(R.id.et_number_questions)
        rgMultipleChoice = view.findViewById(R.id.rg_multiple_choice)
        rbYes = view.findViewById(R.id.rb_yes)
        rbNo = view.findViewById(R.id.rb_no)
        etMinutes = view.findViewById(R.id.et_minutes)
        rvTests = view.findViewById(R.id.rvTests)

        setupSubjectSpinner()
        setupRecycler()
        loadTests()

        ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

        btnAdd.setOnClickListener {
            if (currentEditingTest != null) {
                updateTest()
            } else {
                createTest()
            }
        }

        btnEdit.setOnClickListener {
            if (tests.isEmpty()) {
                Toast.makeText(requireContext(), "Không có đề để sửa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectionMode = SelectionMode.EDIT
            tvMode.text = getString(R.string.choose_test_to_edit)
            Toast.makeText(requireContext(), "Chạm vào đề trong danh sách để sửa", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            if (tests.isEmpty()) {
                Toast.makeText(requireContext(), "Không có đề để xóa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectionMode = SelectionMode.DELETE
            tvMode.text = getString(R.string.choose_test_to_delete)
            Toast.makeText(requireContext(), "Chạm vào đề trong danh sách để xóa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSubjectSpinner() {
        subjects = dbHelper.getAllSubjects()
        val subjectNames = subjects.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter
    }

    private fun setupRecycler() {
        adapter = TestAdapter(tests) { test ->
            when (selectionMode) {
                SelectionMode.EDIT -> {
                    populateFormForEditing(test)
                    selectionMode = SelectionMode.NONE
                    tvMode.text = getString(R.string.mode_create)
                }
                SelectionMode.DELETE -> {
                    confirmDelete(test)
                    selectionMode = SelectionMode.NONE
                    tvMode.text = getString(R.string.mode_create)
                }
                SelectionMode.NONE -> {
                    // quick tap: populate for quick edit
                    populateFormForEditing(test)
                }
            }
        }
        rvTests.layoutManager = LinearLayoutManager(requireContext())
        rvTests.adapter = adapter
    }

    private fun loadTests() {
        tests.clear()
        tests.addAll(dbHelper.getAllTests())
        adapter?.notifyDataSetChanged()
    }

    private fun populateFormForEditing(test: Test) {
        currentEditingTest = test
        tvMode.text = getString(R.string.mode_editing, test.name)
        etTestName.setText(test.name)

        val subjectPos = subjects.indexOfFirst { it.id == test.subjectId }
        if (subjectPos >= 0) spinnerSubject.setSelection(subjectPos)

        cbEasy.isChecked = test.easyPercent > 0
        cbMedium.isChecked = test.mediumPercent > 0
        cbHard.isChecked = test.hardPercent > 0

        etNumberQuestions.setText(test.numQuestions.toString())
        etMinutes.setText(test.durationMinutes.toString())
        if (test.allowMultipleAnswers) rbYes.isChecked = true else rbNo.isChecked = true

        btnAdd.text = getString(R.string.cap_nhat)
    }

    private fun confirmDelete(test: Test) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.xoa_de))
            .setMessage("Bạn có chắc muốn xóa '${test.name}'?")
            .setPositiveButton("Xóa") { dialog, _ ->
                val rows = dbHelper.deleteTest(test.testId)
                if (rows > 0) {
                    Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show()
                    loadTests()
                } else {
                    Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun updateTest() {
        val test = currentEditingTest ?: return

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

        val remainder = 100 % totalChecked
        if (remainder > 0 && checkedDifficulties.isNotEmpty()) {
            when (checkedDifficulties.first().id) {
                R.id.cb_easy -> easyPercent += remainder
                R.id.cb_medium -> mediumPercent += remainder
                R.id.cb_hard -> hardPercent += remainder
            }
        }

        val name = etTestName.text.toString().ifEmpty { test.name }

        val updatedTest = Test(
            testId = test.testId,
            subjectId = selectedSubject.id,
            name = name,
            numQuestions = questionCount,
            durationMinutes = minutes,
            allowMultipleAnswers = allowMultipleAnswers,
            easyPercent = easyPercent,
            mediumPercent = mediumPercent,
            hardPercent = hardPercent,
            createdAt = test.createdAt
        )

        val rows = dbHelper.updateTest(test.testId, updatedTest)
        if (rows > 0) {
            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            currentEditingTest = null
            btnAdd.text = getString(R.string.them)
            tvMode.text = getString(R.string.mode_create)
            etTestName.setText("")
            loadTests()
        } else {
            Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createTest() {
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

        val remainder = 100 % totalChecked
        if (remainder > 0 && checkedDifficulties.isNotEmpty()) {
            when (checkedDifficulties.first().id) {
                R.id.cb_easy -> easyPercent += remainder
                R.id.cb_medium -> mediumPercent += remainder
                R.id.cb_hard -> hardPercent += remainder
            }
        }

        val nameFromField = etTestName.text.toString().ifEmpty { "Đề thi môn ${selectedSubject.name}" }

        val newTest = Test(
            subjectId = selectedSubject.id,
            name = nameFromField,
            numQuestions = questionCount,
            durationMinutes = minutes,
            allowMultipleAnswers = allowMultipleAnswers,
            easyPercent = easyPercent,
            mediumPercent = mediumPercent,
            hardPercent = hardPercent
        )

        val result = dbHelper.addTest(newTest)

        if (result != -1L) {
            Toast.makeText(requireContext(), "Tạo đề thi thành công!", Toast.LENGTH_SHORT).show()
            etTestName.setText("")
            clearForm()
            loadTests()
        } else {
            Toast.makeText(requireContext(), "Lỗi khi tạo đề thi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        cbEasy.isChecked = false
        cbMedium.isChecked = false
        cbHard.isChecked = false
        etNumberQuestions.setText("")
        etMinutes.setText("")
        rbNo.isChecked = true
    }
}
