package com.example.da.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.da.R

class MainAnswerFragment : Fragment() {

    private var answers: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            answers = it.getStringArrayList("answers") ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_answer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        val btnBackMenu = view.findViewById<Button>(R.id.btnBackMenu)
        val layoutAnswers = view.findViewById<LinearLayout>(R.id.layoutAnswers)

        // 1. BACK: trở lại fragment trước
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 2. TRỞ VỀ MENU (HomeFragment)
        btnBackMenu.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // 3. HIỂN THỊ ĐÁP ÁN
        for ((index, ans) in answers.withIndex()) {
            val tv = TextView(requireContext())
            tv.text = "Câu ${index + 1}: $ans"
            tv.textSize = 16f
            tv.setPadding(0, 10, 0, 10)
            layoutAnswers.addView(tv)
        }
    }

    companion object {
        fun newInstance(answers: List<String>): MainAnswerFragment {
            val fragment = MainAnswerFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("answers", ArrayList(answers))
            fragment.arguments = bundle
            return fragment
        }
    }
}
