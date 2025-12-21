package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.model.Test

class TestAdapter(
    private var testList: List<Test>,
    private val onItemClick: (Test) -> Unit
) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = testList[position]
        holder.bind(test)
        holder.itemView.setOnClickListener {
            onItemClick(test)
        }
    }

    override fun getItemCount(): Int {
        return testList.size
    }

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ánh xạ tất cả các TextView cần thiết
        private val tvTestName: TextView = itemView.findViewById(R.id.tvTestName)
        private val tvQuestionCount: TextView = itemView.findViewById(R.id.tvQuestionCount) // <-- Ánh xạ TextView số câu
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)

        fun bind(test: Test) {
            tvTestName.text = test.name

            tvQuestionCount.text = "${test.numQuestions} câu"

            tvDuration.text = "${test.durationMinutes} Phút"
        }
    }
}
