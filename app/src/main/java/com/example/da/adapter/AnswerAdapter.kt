package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question

class AnswerAdapter(private val questions: List<Question>, private val dbHelper: DatabaseHelper) :
    RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question, position, dbHelper)
    }

    override fun getItemCount(): Int = questions.size

    class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestionTitle: TextView = itemView.findViewById(R.id.tvQuestionTitle)
        private val tvQuestionText: TextView = itemView.findViewById(R.id.tvQuestionText)
        private val tvAnswerText: TextView = itemView.findViewById(R.id.tvAnswerText)

        fun bind(question: Question, position: Int, dbHelper: DatabaseHelper) {
            tvQuestionTitle.text = "Câu hỏi ${position + 1}"
            tvQuestionText.text = question.text

            val correctAnswers = dbHelper.getAnswersByQuestion(question.id).filter { it.isCorrect }
            val answerString = correctAnswers.joinToString(separator = ", ") { it.answerText }
            tvAnswerText.text = answerString
        }
    }
}
