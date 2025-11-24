package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.model.Subject

class SubjectAdapter(
    private var subjects: List<Subject>,
    private val onEditClick: (Subject) -> Unit,
    private val onDeleteClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.bind(subject, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = subjects.size

    fun updateSubjects(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(subject: Subject, onEditClick: (Subject) -> Unit, onDeleteClick: (Subject) -> Unit) {
            tvSubjectName.text = subject.name
            btnEdit.setOnClickListener { onEditClick(subject) }
            btnDelete.setOnClickListener { onDeleteClick(subject) }
        }
    }
}

