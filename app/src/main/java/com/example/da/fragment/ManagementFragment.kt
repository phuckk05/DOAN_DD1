package com.example.da.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.QuestionAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question
import com.example.da.model.Subject
import com.google.android.material.button.MaterialButton
import kotlin.text.replace

class ManagementFragment : Fragment() {
    private lateinit var adapter: QuestionAdapter
    private lateinit var dbHelper: DatabaseHelper
    private val allQuestions = mutableListOf<Question>()
    private val subjectsList = mutableListOf<Subject>()
    private val tabViews = mutableListOf<TextView>()
    private var currentSubjectId: Int = -1 // -1 = Tất cả

    private lateinit var tabContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // --- KHỞI TẠO CÁC THÀNH PHẦN CƠ BẢN ---
            dbHelper = DatabaseHelper(requireContext())
            tabContainer = view.findViewById(R.id.tabContainer)

            // --- CÀI ĐẶT RecyclerView ---
            setupRecyclerView(view)

            // --- XỬ LÝ SỰ KIỆN CHO CÁC NÚT ĐIỀU HƯỚNG ---
            setupNavigationButtons(view)

            // --- TẢI DỮ LIỆU LẦN ĐẦU ---
            loadSubjectsAndCreateTabs()
            loadQuestionsForCurrentTab()

        } catch (e: Exception) {
            Log.e("ManagementFragment", "Lỗi nghiêm trọng trong onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Lỗi khởi tạo màn hình: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Khi quay lại màn hình này, tải lại dữ liệu để cập nhật thay đổi (ví dụ: vừa thêm môn học mới)
        if (::dbHelper.isInitialized) {
            loadSubjectsAndCreateTabs()
            loadQuestionsForCurrentTab()
        }
    }

    private fun setupRecyclerView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rvQuestions)
        adapter = QuestionAdapter(mutableListOf()) { question ->
            // Xử lý sự kiện xóa câu hỏi
            showDeleteConfirmationDialog(question)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    private fun setupNavigationButtons(view: View) {
        // Nút "Xem Lịch Sử"
        val btnGoToHistory = view.findViewById<MaterialButton>(R.id.btn_goto_history)
        btnGoToHistory.setOnClickListener { navigateTo(HistoryFragment()) }

        // Nút "Tạo Câu Hỏi"
        val btnCreateQuestion = view.findViewById<MaterialButton>(R.id.btnCreateQuestion)
        btnCreateQuestion.setOnClickListener { navigateTo(TaoCauHoiFragment()) }

        // Nút "Quản lý Môn học"
        val btnManageSubjects = view.findViewById<MaterialButton>(R.id.btnManageSubjects)
        btnManageSubjects.setOnClickListener { navigateTo(SubjectManagementFragment()) }
    }

    private fun loadSubjectsAndCreateTabs() {
        subjectsList.clear()
        subjectsList.addAll(dbHelper.getAllSubjects())

        // Tạo lại các tab động
        tabContainer.removeAllViews()
        tabViews.clear()

        // Thêm tab "Tất cả"
        val allTab = createTabView("Tất cả", -1)
        tabContainer.addView(allTab)
        tabViews.add(allTab)

        // Thêm tab cho từng môn học
        for (subject in subjectsList) {
            val tabView = createTabView(subject.name, subject.id)
            tabContainer.addView(tabView)
            tabViews.add(tabView)
        }

        // Chọn lại tab đang active
        updateTabSelection()
    }

    private fun createTabView(text: String, subjectId: Int): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10))
            textSize = 14f
            setBackgroundResource(R.drawable.tab_unselected_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            elevation = dpToPx(2).toFloat()
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(12)
            }
            setOnClickListener {
                currentSubjectId = subjectId
                updateTabSelection()
                loadQuestionsForCurrentTab()
            }
        }
    }

    private fun updateTabSelection() {
        val currentTabLabel = if (currentSubjectId == -1) "Tất cả" else subjectsList.find { it.id == currentSubjectId }?.name
        tabViews.forEach { tab ->
            val isSelected = tab.text.toString() == currentTabLabel
            tab.setBackgroundResource(if (isSelected) R.drawable.tab_selected_bg else R.drawable.tab_unselected_bg)
            tab.setTextColor(ContextCompat.getColor(requireContext(), if (isSelected) android.R.color.white else R.color.black))
            tab.elevation = dpToPx(if (isSelected) 6 else 2).toFloat()
            tab.animate()
                .scaleX(if (isSelected) 1.05f else 1.0f)
                .scaleY(if (isSelected) 1.05f else 1.0f)
                .setDuration(200)
                .start()
        }
    }

    private fun loadQuestionsForCurrentTab() {
        if (!::adapter.isInitialized) return
        try {
            allQuestions.clear()
            val questions = if (currentSubjectId == -1) {
                dbHelper.getAllQuestions()
            } else {
                dbHelper.getQuestionsBySubject(currentSubjectId)
            }
            allQuestions.addAll(questions)
            adapter.updateQuestions(allQuestions.toList()) // Cập nhật adapter với danh sách mới
        } catch (e: Exception) {
            Log.e("ManagementFragment", "Lỗi tải câu hỏi: ${e.message}", e)
            Toast.makeText(requireContext(), "Lỗi tải câu hỏi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(question: Question) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa câu hỏi này không?")
            .setPositiveButton("Xóa") { _, _ ->
                try {
                    dbHelper.deleteQuestion(question.id)
                    Toast.makeText(requireContext(), "Đã xóa câu hỏi", Toast.LENGTH_SHORT).show()
                    loadQuestionsForCurrentTab() // Tải lại danh sách sau khi xóa
                } catch (e: Exception) {
                    Log.e("ManagementFragment", "Lỗi khi xóa câu hỏi: ${e.message}", e)
                    Toast.makeText(requireContext(), "Lỗi khi xóa: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            // Thay thế bằng ID container chuẩn trong MainActivity
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }
}
