
package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private lateinit var answersContainer: LinearLayout
    private lateinit var rgMultipleChoice: RadioGroup
    private var subjectsList = mutableListOf<Subject>()

    // List to store answer views (CompoundButton supports both CheckBox and RadioButton)
    private val answerViews = mutableListOf<Triple<CompoundButton, EditText, View?>>()

    // Track if multiple choice is allowed
    private var isMultipleChoice = false

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
        answersContainer = view.findViewById(R.id.answersContainer)
        rgMultipleChoice = view.findViewById(R.id.rgMultipleChoice)

        // Set default to "Không" (single choice) - rbNo should be checked by default in XML
        // Get initial state from RadioGroup
        val checkedId = rgMultipleChoice.checkedRadioButtonId
        isMultipleChoice = when (checkedId) {
            R.id.rbYes -> true
            R.id.rbNo -> false
            else -> false // default to single choice if nothing checked
        }

        // Setup multiple choice listener
        rgMultipleChoice.setOnCheckedChangeListener { _, newCheckedId ->
            when (newCheckedId) {
                R.id.rbYes -> {
                    if (!isMultipleChoice) { // Only convert if mode actually changed
                        isMultipleChoice = true
                        convertAnswersToCheckBoxes()
                    }
                }
                R.id.rbNo -> {
                    if (isMultipleChoice) { // Only convert if mode actually changed
                        isMultipleChoice = false
                        convertAnswersToRadioButtons()
                    }
                }
            }
        }

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
        // Check limit when not multiple choice (max 4 answers)
        if (!isMultipleChoice && answerViews.size >= 4) {
            Toast.makeText(
                requireContext(),
                "Chế độ chọn 1 chỉ cho phép tối đa 4 đáp án!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create a new LinearLayout for the answer
        val answerLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        // Create RadioButton or CheckBox based on mode
        val selectionButton: CompoundButton = if (isMultipleChoice) {
            CheckBox(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        } else {
            RadioButton(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // Uncheck other radio buttons when this is checked
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        answerViews.forEach { (button, _, _) ->
                            if (button != this && button is RadioButton) {
                                button.isChecked = false
                            }
                        }
                    }
                }
            }
        }

        // Create EditText
        val editText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = dpToPx(8)
            }
            hint = "Nhập đáp án ${answerViews.size + 1}"
            textSize = 14f
            setBackgroundResource(android.R.color.transparent)
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        // Create Delete button (X icon)
        val deleteButton = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(32),
                dpToPx(32)
            ).apply {
                marginStart = dpToPx(8)
            }
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            contentDescription = "Xóa đáp án"
        }

        // Add views to layout
        answerLayout.addView(selectionButton)
        answerLayout.addView(editText)
        answerLayout.addView(deleteButton)

        // Add to answerViews list with parent layout reference
        answerViews.add(Triple(selectionButton, editText, answerLayout))

        // Set delete button click listener
        deleteButton.setOnClickListener {
            if (answerViews.size > 1) {
                removeAnswerField(answerLayout, selectionButton, editText)
            } else {
                Toast.makeText(requireContext(), "Phải có ít nhất 1 đáp án!", Toast.LENGTH_SHORT).show()
            }
        }

        // Find the index of tvAddAnswer and insert before it - with null safety
        val tvAddAnswer = answersContainer.findViewById<TextView>(R.id.tvAddAnswer)
        if (tvAddAnswer == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy nút thêm đáp án", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerParent = tvAddAnswer.parent as? View
        if (addAnswerParent == null) {
            Toast.makeText(requireContext(), "Lỗi: Không thể thêm đáp án", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerIndex = answersContainer.indexOfChild(addAnswerParent)
        if (addAnswerIndex < 0) {
            // If can't find index, add to the end before last child
            answersContainer.addView(answerLayout, answersContainer.childCount - 1)
        } else {
            answersContainer.addView(answerLayout, addAnswerIndex)
        }

        Toast.makeText(
            requireContext(),
            "Đã thêm đáp án ${answerViews.size}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun removeAnswerField(layout: View, button: CompoundButton, editText: EditText) {
        // Find and remove from answerViews list
        val iterator = answerViews.iterator()
        while (iterator.hasNext()) {
            val (btn, et, _) = iterator.next()
            if (btn == button && et == editText) {
                iterator.remove()
                break
            }
        }

        // Remove from container
        answersContainer.removeView(layout)

        Toast.makeText(
            requireContext(),
            "Đã xóa đáp án. Còn ${answerViews.size} đáp án",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun convertAnswersToCheckBoxes() {
        // Convert all RadioButtons to CheckBoxes
        val currentAnswers = answerViews.toList()
        answerViews.clear()

        // Find the add answer button and its parent safely
        val tvAddAnswer = answersContainer.findViewById<TextView>(R.id.tvAddAnswer)
        if (tvAddAnswer == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy nút thêm đáp án", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerParent = tvAddAnswer.parent as? View
        if (addAnswerParent == null) {
            Toast.makeText(requireContext(), "Lỗi: Không thể chuyển đổi", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerIndex = answersContainer.indexOfChild(addAnswerParent)
        if (addAnswerIndex < 0) {
            Toast.makeText(requireContext(), "Lỗi: Vị trí không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Remove all answer views from container (keep only tvAddAnswer parent)
        try {
            for (i in answersContainer.childCount - 1 downTo 0) {
                val child = answersContainer.getChildAt(i)
                if (child != addAnswerParent) {
                    answersContainer.removeViewAt(i)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi khi xóa đáp án cũ: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        // Recreate answers with CheckBoxes
        // After removing all answers, only tvAddAnswer parent remains
        // Insert at position 0 (before tvAddAnswer)
        for ((oldButton, editText, _) in currentAnswers) {
            try {
                recreateAnswerWithNewButton(editText.text.toString(), oldButton.isChecked, true, 0)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi khi tạo đáp án: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertAnswersToRadioButtons() {
        // Convert all CheckBoxes to RadioButtons (max 4)
        val currentAnswers = answerViews.toList()

        // Check if need to remove excess answers
        if (currentAnswers.size > 4) {
            Toast.makeText(
                requireContext(),
                "Chế độ chọn 1 chỉ cho phép tối đa 4 đáp án. Giữ lại 4 đáp án đầu tiên.",
                Toast.LENGTH_LONG
            ).show()
        }

        answerViews.clear()

        // Find the add answer button and its parent safely
        val tvAddAnswer = answersContainer.findViewById<TextView>(R.id.tvAddAnswer)
        if (tvAddAnswer == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy nút thêm đáp án", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerParent = tvAddAnswer.parent as? View
        if (addAnswerParent == null) {
            Toast.makeText(requireContext(), "Lỗi: Không thể chuyển đổi", Toast.LENGTH_SHORT).show()
            return
        }

        val addAnswerIndex = answersContainer.indexOfChild(addAnswerParent)
        if (addAnswerIndex < 0) {
            Toast.makeText(requireContext(), "Lỗi: Vị trí không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Remove all answer views from container (keep only tvAddAnswer parent)
        try {
            for (i in answersContainer.childCount - 1 downTo 0) {
                val child = answersContainer.getChildAt(i)
                if (child != addAnswerParent) {
                    answersContainer.removeViewAt(i)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi khi xóa đáp án cũ: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        // Recreate answers with RadioButtons (max 4)
        // After removing all answers, only tvAddAnswer parent remains at index 0
        // So we insert all new answers at position 0 (before tvAddAnswer)
        var hasChecked = false
        val answersToKeep = currentAnswers.take(4)

        for (triple in answersToKeep) {
            try {
                val (oldButton, editText, _) = triple
                val isChecked = if (!hasChecked && oldButton.isChecked) {
                    hasChecked = true
                    true
                } else {
                    false
                }
                // Insert at position 0 (will push previous answers down)
                recreateAnswerWithNewButton(editText.text.toString(), isChecked, false, 0)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi khi tạo đáp án: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // If no answers were kept (all deleted), show message
        if (answersToKeep.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Chưa có đáp án nào. Vui lòng thêm đáp án!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun recreateAnswerWithNewButton(text: String, isChecked: Boolean, useCheckBox: Boolean, insertIndex: Int) {
        val answerLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val selectionButton: CompoundButton = if (useCheckBox) {
            CheckBox(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                this.isChecked = isChecked
            }
        } else {
            RadioButton(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                this.isChecked = isChecked
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        answerViews.forEach { (button, _, _) ->
                            if (button != this && button is RadioButton) {
                                button.isChecked = false
                            }
                        }
                    }
                }
            }
        }

        val editText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = dpToPx(8)
            }
            setText(text)
            hint = "Nhập đáp án ${answerViews.size + 1}"
            textSize = 14f
            setBackgroundResource(android.R.color.transparent)
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        val deleteButton = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(32),
                dpToPx(32)
            ).apply {
                marginStart = dpToPx(8)
            }
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            contentDescription = "Xóa đáp án"
        }

        answerLayout.addView(selectionButton)
        answerLayout.addView(editText)
        answerLayout.addView(deleteButton)

        answerViews.add(Triple(selectionButton, editText, answerLayout))

        deleteButton.setOnClickListener {
            if (answerViews.size > 1) {
                removeAnswerField(answerLayout, selectionButton, editText)
            } else {
                Toast.makeText(requireContext(), "Phải có ít nhất 1 đáp án!", Toast.LENGTH_SHORT).show()
            }
        }

        answersContainer.addView(answerLayout, insertIndex)
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

        // Collect answers
        val answers = mutableListOf<Pair<String, Boolean>>()
        var hasCorrectAnswer = false

        for ((checkbox, editText, _) in answerViews) {
            val answerText = editText.text.toString().trim()
            if (answerText.isNotEmpty()) {
                val isCorrect = checkbox.isChecked
                answers.add(Pair(answerText, isCorrect))
                if (isCorrect) hasCorrectAnswer = true
            }
        }

        // Validate answers
        if (answers.size < 1) {
            Toast.makeText(
                requireContext(),
                "Vui lòng nhập ít nhất 1 đáp án!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!hasCorrectAnswer) {
            Toast.makeText(
                requireContext(),
                "Vui lòng chọn ít nhất 1 đáp án đúng!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Additional validation for single choice mode
        if (!isMultipleChoice) {
            val correctCount = answers.count { it.second }
            if (correctCount > 1) {
                Toast.makeText(
                    requireContext(),
                    "Chế độ chọn 1 chỉ cho phép 1 đáp án đúng!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val selectedSubject = subjectsList[spinnerSubject.selectedItemPosition]

        // Save to database
        val questionId = dbHelper.addQuestion(
            subjectId = selectedSubject.id,
            questionText = question,
            difficulty = difficulty,
            isMultipleChoice = isMultipleChoice,
            answers = answers
        )

        if (questionId > 0) {
            Toast.makeText(
                requireContext(),
                "Lưu câu hỏi cho môn '${selectedSubject.name}' thành công!",
                Toast.LENGTH_SHORT
            ).show()

            // Clear inputs
            etQuestion.text.clear()
            for ((checkbox, editText, _) in answerViews) {
                editText.text.clear()
                checkbox.isChecked = false
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Lỗi khi lưu câu hỏi!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

