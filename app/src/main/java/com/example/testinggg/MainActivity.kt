package com.example.testinggg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_search -> SearchFragment()
                R.id.navigation_calendar -> CalendarFragment()
                // R.id.navigation_profile -> ProfileFragment() // 이 부분을 기능 구현 후 활성화
                else -> null
            }
            fragment?.let {
                loadFragment(it)
                true // 프래그먼트 로딩이 성공적으로 시작되면 true 반환
            } ?: false // null이면 false 반환
        }

        // 앱 시작 시 HomeFragment를 기본으로 로드
        if (savedInstanceState == null) {
            navView.selectedItemId = R.id.navigation_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
