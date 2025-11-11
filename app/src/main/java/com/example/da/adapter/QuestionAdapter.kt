package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.model.Question

class QuestionAdapter(
    private val questions: List<Question>,
    private val onDelete: (Question) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private var displayList = questions.toList()

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
        val tvDifficulty: TextView = view.findViewById(R.id.tvDifficulty)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = displayList[position]
        holder.tvQuestion.text = question.text
        holder.tvDifficulty.text = "Mức độ: ${question.difficulty}"

        holder.ivDelete.setOnClickListener {
            onDelete(question)
        }
    }

    override fun getItemCount(): Int = displayList.size

    fun submitList(newList: List<Question>) {
        displayList = newList
        notifyDataSetChanged()
    }
}

