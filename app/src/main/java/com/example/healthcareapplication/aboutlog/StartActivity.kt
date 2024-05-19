package com.example.healthcareapplication.aboutlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.healthcareapplication.MainActivity
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.ActivityStartBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)

        binding.noAccountBtn.setOnClickListener{
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                        //수정 필요함
                        Log.e("FirebaseAuth", "signInAnonymously:failure", task.exception)
                        Toast.makeText(this, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}