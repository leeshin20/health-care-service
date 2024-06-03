package com.example.healthcareapplication.data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContentData {
    companion object {
        private val database = Firebase.database

        val boardRef = database.getReference("contents")
    }
}