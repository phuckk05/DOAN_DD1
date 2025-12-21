package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.adapter.SubjectAdapter
import com.example.da.database.DatabaseHelper
import com.example.da.model.Subject

class SubjectManagementFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var subjectAdapter: SubjectAdapter
    private val subjectsList = mutableListOf<Subject>()

    private lateinit var btnBack: ImageButton
    private lateinit var rvSubjects: RecyclerView
    private lateinit var btnAddSubject: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subject_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        setControl(view)
        setEvent()

        // Initial data load
        loadSubjects()
    }

    // Initialize views
    private fun setControl(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        rvSubjects = view.findViewById(R.id.rvSubjects)
        btnAddSubject = view.findViewById(R.id.btnAddSubject)

        subjectAdapter = SubjectAdapter(subjectsList,
            onEditClick = { subject -> showEditSubjectDialog(subject) },
            onDeleteClick = { subject -> showDeleteConfirmationDialog(subject) }
        )

        rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        rvSubjects.adapter = subjectAdapter
    }

    // Setup event listeners
    private fun setEvent() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnAddSubject.setOnClickListener {
            showAddSubjectDialog()
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

    private fun loadSubjects() {
        subjectsList.clear()
        subjectsList.addAll(dbHelper.getAllSubjects())
        subjectAdapter.updateSubjects(subjectsList)
    }

    private fun showAddSubjectDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thêm Môn học")

        val input = EditText(requireContext())
        input.hint = "Nhập tên môn học"
        builder.setView(input)

        builder.setPositiveButton("Thêm") { dialog, _ ->
            val subjectName = input.text.toString().trim()
            if (subjectName.isNotEmpty()) {
                if (!dbHelper.isSubjectExists(subjectName)) {
                    val newSubject = Subject(name = subjectName, createdAt = System.currentTimeMillis())
                    dbHelper.addSubject(newSubject)
                    loadSubjects()
                    Toast.makeText(requireContext(), "Đã thêm môn học", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Môn học đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Tên môn học không được để trống", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showEditSubjectDialog(subject: Subject) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sửa tên Môn học")

        val input = EditText(requireContext())
        input.setText(subject.name)
        builder.setView(input)

        builder.setPositiveButton("Lưu") { dialog, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                if (!dbHelper.isSubjectExists(newName) || newName.equals(subject.name, ignoreCase = true)) {
                    dbHelper.updateSubject(subject.id, newName)
                    loadSubjects()
                    Toast.makeText(requireContext(), "Đã cập nhật môn học", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Tên môn học đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Tên môn học không được để trống", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showDeleteConfirmationDialog(subject: Subject) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận Xóa")
            .setMessage("Bạn có chắc chắn muốn xóa môn học '${subject.name}' không? Tất cả các câu hỏi liên quan cũng sẽ bị xóa.")
            .setPositiveButton("Xóa") { _, _ ->
                dbHelper.deleteSubjectAndQuestions(subject.id)
                loadSubjects()
                Toast.makeText(requireContext(), "Đã xóa môn học", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
