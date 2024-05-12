package com.example.testinggg

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testinggg.R
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 현재 날짜를 가져와서 TextView에 설정
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.tvDate).text = currentDate
    }
}
