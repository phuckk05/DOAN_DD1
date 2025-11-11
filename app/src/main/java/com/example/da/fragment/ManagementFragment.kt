package com.example.da.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.adapter.QuestionAdapter
import com.example.da.model.Question
import com.google.android.material.button.MaterialButton

class ManagementFragment : Fragment() {
    private lateinit var adapter: QuestionAdapter
    private val allQuestions = mutableListOf<Question>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreate = view.findViewById<MaterialButton>(R.id.btnCreateQuestion)
        val ivAdd = view.findViewById<ImageView>(R.id.ivAddQuestion)
        val rv = view.findViewById<RecyclerView>(R.id.rvQuestions)

        // Sample data (subject matches tabs text lowercase, simplified)
        if (allQuestions.isEmpty()) {
            allQuestions.addAll(
                listOf(
                    Question(1, "Tiếng anh", "What is english ?", "Dễ"),
                    Question(2, "Toán học", "2 + 2 = ?", "Dễ"),
                    Question(3, "Lịch sử", "Ai là vua đầu tiên?", "Vừa"),
                    Question(4, "Địa lí", "Thủ đô Việt Nam?", "Dễ"),
                    Question(5, "Hóa học", "H2O là gì?", "Dễ"),
                    Question(6, "Tiếng anh", "Translate 'Hello'?", "Dễ"),
                )
            )
        }

        adapter = QuestionAdapter(allQuestions) { q ->
            allQuestions.remove(q)
            filterAndDisplay(currentTab)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val tabs = listOf(
            view.findViewById<TextView>(R.id.tabAll),
            view.findViewById<TextView>(R.id.tabEnglish),
            view.findViewById<TextView>(R.id.tabMath),
            view.findViewById<TextView>(R.id.tabHistory),
            view.findViewById<TextView>(R.id.tabGeography),
            view.findViewById<TextView>(R.id.tabChemistry),
        )

        fun selectTab(selected: TextView) {
            tabs.forEach { tab ->
                if (tab == selected) {
                    tab.setBackgroundResource(R.drawable.tab_selected_bg)
                    tab.setTextColor(0xFFFFFFFF.toInt())
                } else {
                    tab.background = null
                    tab.setTextColor(0xFF111111.toInt())
                }
            }
            currentTab = selected.text.toString()
            filterAndDisplay(currentTab)
        }

        tabs.forEach { tab ->
            tab.setOnClickListener { selectTab(tab) }
        }

        // Ensure first tab looks selected initially
        selectTab(tabs.first())

        btnCreate.setOnClickListener { navigateToCreate(TaoCauHoiFragment()) }
        ivAdd.setOnClickListener { navigateToCreate(TaoMonHocFragment()) }
    }

    private var currentTab: String = "Tất cả"

    private fun filterAndDisplay(tabName: String) {
        val filtered = if (tabName == "Tất cả") allQuestions else allQuestions.filter { it.subject == tabName }
        adapter.submitList(filtered)
    }

    //truyền vào fragment tạo câu hỏi
    private fun navigateToCreate(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
