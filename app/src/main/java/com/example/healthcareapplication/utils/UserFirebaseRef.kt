package com.example.healthcareapplication.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FireBaseRef {
    companion object {
        val database = Firebase.database
        val userInfo = database.getReference("UserInfo")

        val boardRef = database.getReference("board")
    }
}