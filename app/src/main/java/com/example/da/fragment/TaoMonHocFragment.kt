package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.da.R
import com.example.da.activity.MainActivity
import com.example.da.database.DatabaseHelper
import com.example.da.model.Subject

class TaoMonHocFragment : Fragment() {

    private lateinit var etSubjectName: EditText
    private lateinit var tvAddSubject: TextView
    private lateinit var ivBack: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tao_mon_hoc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        dbHelper = DatabaseHelper(requireContext())

        setControl(view)
        setEvent()
    }

    // Initialize views
    private fun setControl(view: View) {
        etSubjectName = view.findViewById(R.id.etSubjectName)
        tvAddSubject = view.findViewById(R.id.tvAddSubject)
        ivBack = view.findViewById(R.id.ivBackSubject)
    }

    // Setup event listeners
    private fun setEvent() {
        // Setup click listeners
        tvAddSubject.setOnClickListener {
            addNewSubject()
        }

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
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

    private fun addNewSubject() {
        val subjectName = etSubjectName.text.toString().trim()

        // Validation
        if (subjectName.isEmpty()) {
            etSubjectName.error = "Vui lòng nhập tên môn học"
            etSubjectName.requestFocus()
            return
        }

        if (subjectName.length < 2) {
            etSubjectName.error = "Tên môn học phải có ít nhất 2 ký tự"
            etSubjectName.requestFocus()
            return
        }

        // Check if subject already exists
        if (dbHelper.isSubjectExists(subjectName)) {
            etSubjectName.error = "Môn học này đã tồn tại"
            etSubjectName.requestFocus()
            return
        }

        // Save to database
        val subject = Subject(name = subjectName, createdAt = System.currentTimeMillis())
        val id = dbHelper.addSubject(subject)

        if (id > 0) {
            Toast.makeText(
                requireContext(),
                "Thêm môn học '$subjectName' thành công!",
                Toast.LENGTH_SHORT
            ).show()

            // Clear fields
            etSubjectName.text.clear()

            // Navigate back
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "Có lỗi xảy ra, vui lòng thử lại!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
