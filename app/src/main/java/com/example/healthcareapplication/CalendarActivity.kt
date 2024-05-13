package com.example.healthcareapplication

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val dayText: TextView = findViewById(R.id.day_text)
        val calendarView: CalendarView = findViewById(R.id.calendarView)

        val today = Calendar.getInstance()
        calendarView.date = today.timeInMillis

        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        dayText.text = dateFormat.format(today.time)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            dayText.text = dateFormat.format(calendar.time)
        }
    }
}
