package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R

data class TestItem(
    val id: Int,
    val name: String,
    val type: String,
    val duration: String,
    val iconResId: Int
)

class TestAdapter(private val testList: List<TestItem>) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTestName: TextView = itemView.findViewById(R.id.tvTestName)
        private val tvTestType: TextView = itemView.findViewById(R.id.tvTestType)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)

        fun bind(test: TestItem) {
            tvTestName.text = test.name
            tvTestType.text = test.type
            tvDuration.text = test.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(testList[position])
    }

    override fun getItemCount(): Int = testList.size
}

