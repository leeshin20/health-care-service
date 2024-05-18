package com.example.healthcareapplication.aboutlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcareapplication.MainActivity
import com.example.healthcareapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoadingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        auth = Firebase.auth

        if(auth.currentUser?.uid == null) {
            android.os.Handler().postDelayed({
                startActivity(Intent(this, StartActivity::class.java))
                finish()
            }, 3000)
        } else{
            Log.d("SplashActivity", "not null")
            android.os.Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 3000)
        }
    }
}