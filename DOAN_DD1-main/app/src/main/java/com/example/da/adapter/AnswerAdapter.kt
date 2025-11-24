package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.database.DatabaseHelper
import com.example.da.model.Question

class AnswerAdapter(private val questions: List<Question>, private val dbHelper: DatabaseHelper) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return AnswerViewHolder(view, dbHelper)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question, position + 1)
    }

    override fun getItemCount(): Int = questions.size

    class AnswerViewHolder(itemView: View, private val dbHelper: DatabaseHelper) : RecyclerView.ViewHolder(itemView) {
        private val questionNumberTextView: TextView = itemView.findViewById(R.id.tvQuestionNumber)
        private val questionTextView: TextView = itemView.findViewById(R.id.tvQuestionText)
        private val answerTextView: TextView = itemView.findViewById(R.id.tvAnswerText)

        fun bind(question: Question, position: Int) {
            questionNumberTextView.text = "Câu hỏi $position"
            questionTextView.text = question.text
            val answers = dbHelper.getAnswersForQuestion(question.id)
            val correctAnswer = answers.find { it.isCorrect }
            answerTextView.text = correctAnswer?.answerText ?: "No correct answer found"
        }
    }
}
