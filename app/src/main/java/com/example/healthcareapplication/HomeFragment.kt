package com.example.healthcareapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        tvDate.text = "$currentDate"

        val tvMealPlan: TextView = view.findViewById(R.id.tvMealPlan)
        tvMealPlan.text = "오늘의 식단: 샐러드, 치킨"

        val tvExercise: TextView = view.findViewById(R.id.tvExercise)
        tvExercise.text = "오늘의 운동: 조깅 30분"
    }
}
