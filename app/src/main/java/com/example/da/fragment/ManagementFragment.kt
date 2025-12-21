package com.example.da.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.SessionManager // <-- THÊM IMPORT
import com.example.da.activity.AuthActivity // <-- THÊM IMPORT
import com.example.da.adapter.QuestionAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question
import com.example.da.model.Subject

class ManagementFragment : Fragment() {
    private lateinit var adapter: QuestionAdapter
    private lateinit var dbHelper: DatabaseHelper
    private val allQuestions = mutableListOf<Question>()
    private val subjectsList = mutableListOf<Subject>()
    private val tabViews = mutableListOf<TextView>()
    private var currentSubjectId: Int = -1

    private lateinit var tabContainer: LinearLayout
    private lateinit var rvQuestions: RecyclerView
    private lateinit var btnMenu: ImageView

    private lateinit var sessionManager: SessionManager // <-- THÊM BIẾN SESSION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // KHỞI TẠO SESSION MANAGER
        sessionManager = SessionManager(requireContext())

        // ===== LOGIC BẢO VỆ MÀN HÌNH (QUAN TRỌNG NHẤT) =====
        if (sessionManager.getUserRole() != "admin") {
            // Nếu không phải admin, không cho phép truy cập
            Toast.makeText(requireContext(), "Bạn không có quyền truy cập chức năng này!", Toast.LENGTH_LONG).show()

            // Tự động quay lại màn hình trước đó
            parentFragmentManager.popBackStack()

            // Trả về một View trống hoặc null để không vẽ bất cứ thứ gì
            return null
        }

        // Nếu là admin, tiếp tục tạo view như bình thường
        return inflater.inflate(R.layout.fragment_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // Khởi tạo các thành phần khác
            dbHelper = DatabaseHelper(requireContext())

            setControl(view)
            setEvent()

            // Tải dữ liệu ban đầu
            loadSubjectsAndCreateTabs()
            loadQuestionsForCurrentTab()

        } catch (e: Exception) {
            Log.e("ManagementFragment", "Lỗi nghiêm trọng trong onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Lỗi khởi tạo màn hình: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Ánh xạ views
    private fun setControl(view: View) {
        tabContainer = view.findViewById(R.id.tabContainer)
        rvQuestions = view.findViewById(R.id.rvQuestions)
        btnMenu = view.findViewById(R.id.btnMenu)

        setupRecyclerView()
    }

    // Cài đặt sự kiện
    private fun setEvent() {
        btnMenu.setOnClickListener { showPopupMenu(it) }
    }

    override fun onResume() {
        super.onResume()
        // Kiểm tra dbHelper đã khởi tạo chưa trước khi dùng
        if (::dbHelper.isInitialized) {
            loadSubjectsAndCreateTabs()
            loadQuestionsForCurrentTab()
        }
    }

    private fun setupRecyclerView() {
        adapter = QuestionAdapter(
            mutableListOf(),
            onDelete = { question -> showDeleteConfirmationDialog(question) },
            onItemClick = { question -> navigateTo(TaoCauHoiFragment.newInstance(question.id)) }
        )
        rvQuestions.layoutManager = LinearLayoutManager(requireContext())
        rvQuestions.adapter = adapter
    }

    // THÊM MỤC "ĐĂNG XUẤT" VÀO MENU
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.management_menu, popup.menu)

        // THÊM MỘT ITEM MỚI VÀO MENU (HOẶC TẠO FILE MENU MỚI)
        popup.menu.add(0, R.id.action_logout, 100, "Đăng xuất")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    navigateTo(HistoryFragment())
                    true
                }
                R.id.action_create_question -> {
                    navigateTo(TaoCauHoiFragment())
                    true
                }
                R.id.action_manage_subjects -> {
                    navigateTo(SubjectManagementFragment())
                    true
                }
                R.id.action_logout -> { // <-- XỬ LÝ SỰ KIỆN ĐĂNG XUẤT
                    sessionManager.logoutUser()
                    val intent = Intent(activity, AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity?.finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun loadSubjectsAndCreateTabs() {
        subjectsList.clear()
        subjectsList.addAll(dbHelper.getAllSubjects())

        tabContainer.removeAllViews()
        tabViews.clear()

        val allTab = createTabView("Tất cả", -1)
        tabContainer.addView(allTab)
        tabViews.add(allTab)

        for (subject in subjectsList) {
            val tabView = createTabView(subject.name, subject.id)
            tabContainer.addView(tabView)
            tabViews.add(tabView)
        }
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
            adapter.updateQuestions(allQuestions.toList())
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
                    loadQuestionsForCurrentTab()
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
        parentFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
