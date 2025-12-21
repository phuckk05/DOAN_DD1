package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.model.TestResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class TestHistoryAdapter(
    private val testResults: List<TestResult>,
    private val onViewAnswersClicked: (TestResult) -> Unit) :
    RecyclerView.Adapter<TestHistoryAdapter.TestHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_history, parent, false)
        return TestHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestHistoryViewHolder, position: Int) {
        val result = testResults[position]
        holder.bind(result)
        holder.itemView.findViewById<Button>(R.id.btnViewAnswers).setOnClickListener {
            onViewAnswersClicked(result)
        }
    }

    override fun getItemCount(): Int = testResults.size

    class TestHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvTimeTaken: TextView = itemView.findViewById(R.id.tvTimeTaken)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(result: TestResult) {
            // If score was stored on 0-100 scale, convert to 0-10 for display.
            val display = if (result.score <= 10) {
                "${result.score}/10"
            } else {
                val normalized = (result.score.toDouble() / 10.0).roundToInt()
                "${normalized}/10"
            }
            tvScore.text = "Điểm: $display"
            tvTimeTaken.text = "Thời gian: ${result.timeTakenSeconds} giây"
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val dateString = sdf.format(Date(result.timestamp))
            tvDate.text = "Ngày làm: $dateString"
        }
    }
}
