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

class TaoMonHocFragment : Fragment() {

    private lateinit var etSubjectName: EditText
    private lateinit var tvAddSubject: TextView
    private lateinit var ivBack: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tao_mon_hoc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        etSubjectName = view.findViewById(R.id.etSubjectName)
        tvAddSubject = view.findViewById(R.id.tvAddSubject)
        ivBack = view.findViewById(R.id.ivBackSubject)

        // Setup click listeners
        tvAddSubject.setOnClickListener {
            addNewSubject()
        }

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
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

        // TODO: Save subject to database
        // For now, show success message
        Toast.makeText(
            requireContext(),
            "Thêm môn học '$subjectName' thành công!",
            Toast.LENGTH_SHORT
        ).show()

        // Clear fields
        etSubjectName.text.clear()

        // Navigate back
        parentFragmentManager.popBackStack()
    }
}


