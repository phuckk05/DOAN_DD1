package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.QuestionAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question
import com.example.da.model.Subject
import com.google.android.material.button.MaterialButton

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
            // Initialize database
            dbHelper = DatabaseHelper(requireContext())

            val btnCreate = view.findViewById<MaterialButton>(R.id.btnCreateQuestion)
            val ivAdd = view.findViewById<MaterialButton>(R.id.ivAddQuestion)
            val rv = view.findViewById<RecyclerView>(R.id.rvQuestions)
            tabContainer = view.findViewById<LinearLayout>(R.id.tabContainer)

            // Load subjects from database
            loadSubjects()

            // Create dynamic tabs
            createDynamicTabs()

            // Setup RecyclerView
            adapter = QuestionAdapter(allQuestions) { question ->
                try {
                    // Delete question
                    dbHelper.deleteQuestion(question.id)
                    loadQuestionsForCurrentTab()
                    Toast.makeText(requireContext(), "Đã xóa câu hỏi", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Lỗi khi xóa: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = adapter

            // Load questions for "Tất cả" tab initially
            loadQuestionsForCurrentTab()

            btnCreate?.setOnClickListener { navigateToCreate(TaoCauHoiFragment()) }
            ivAdd?.setOnClickListener { navigateToCreate(TaoMonHocFragment()) }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi khởi tạo: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        // Only reload if adapter is initialized
        if (::adapter.isInitialized && ::dbHelper.isInitialized) {
            loadSubjects()
            createDynamicTabs()
            loadQuestionsForCurrentTab()
        }
    }

    private fun loadSubjects() {
        subjectsList.clear()
        subjectsList.addAll(dbHelper.getAllSubjects())
    }

    private fun createDynamicTabs() {
        tabContainer.removeAllViews()
        tabViews.clear()

        // Add "Tất cả" tab
        val allTab = createTabView("Tất cả", -1)
        tabContainer.addView(allTab)
        tabViews.add(allTab)

        // Add tabs for each subject
        for (subject in subjectsList) {
            val tabView = createTabView(subject.name, subject.id)
            tabContainer.addView(tabView)
            tabViews.add(tabView)
        }

         // Select first tab by default only if adapter is initialized
        if (tabViews.isNotEmpty() && ::adapter.isInitialized) {
            selectTab(tabViews[0], -1)
        } else if (tabViews.isNotEmpty()) {
            // Just highlight first tab without loading questions
            tabViews[0].setBackgroundResource(R.drawable.tab_selected_bg)
            tabViews[0].setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            currentSubjectId = -1
        }
    }

    private fun createTabView(text: String, subjectId: Int): TextView {
        val tabView = TextView(requireContext()).apply {
            this.text = text
            setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10))
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setBackgroundResource(R.drawable.tab_unselected_bg)

            // Add elevation/shadow
            elevation = dpToPx(2).toFloat()

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(12)
                topMargin = dpToPx(4)
                bottomMargin = dpToPx(4)
            }

            // Make text bold
            typeface = android.graphics.Typeface.DEFAULT_BOLD

            setOnClickListener {
                selectTab(this, subjectId)
            }
        }
        return tabView
    }

    private fun selectTab(selected: TextView, subjectId: Int) {
        tabViews.forEach { tab ->
            if (tab == selected) {
                // Selected tab - gradient blue background
                tab.setBackgroundResource(R.drawable.tab_selected_bg)
                tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                tab.elevation = dpToPx(6).toFloat()

                // Scale animation
                tab.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .start()
            } else {
                // Unselected tab - light gray background
                tab.setBackgroundResource(R.drawable.tab_unselected_bg)
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tab.elevation = dpToPx(2).toFloat()

                // Reset scale
                tab.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start()
            }
        }
        currentSubjectId = subjectId
        loadQuestionsForCurrentTab()
    }

    private fun loadQuestionsForCurrentTab() {
        // Check if adapter is initialized
        if (!::adapter.isInitialized) {
            return
        }

        try {
            allQuestions.clear()

            if (currentSubjectId == -1) {
                // Load all questions from all subjects
                for (subject in subjectsList) {
                    val questions = dbHelper.getQuestionsBySubject(subject.id)
                    allQuestions.addAll(questions)
                }
            } else {
                // Load questions for specific subject
                val questions = dbHelper.getQuestionsBySubject(currentSubjectId)
                allQuestions.addAll(dbHelper.getQuestionsBySubject(currentSubjectId))
            }

            adapter.updateQuestions(allQuestions.toList())
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi tải câu hỏi: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            adapter.updateQuestions(emptyList())
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun navigateToCreate(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
