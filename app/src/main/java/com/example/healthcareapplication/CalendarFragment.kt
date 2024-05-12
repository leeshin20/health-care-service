package com.example.healthcareapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Locale

class CalendarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val selectedDateText: TextView = view.findViewById(R.id.selected_date)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
            selectedDateText.text = "Selected date: $date"
        }

        return view
    }
}
