package com.example.healthcareapplication.DATA

import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

class ContentData {
    companion object {
        private val database = Firebase.database

        val boardRef = database.getReference("contents")
    }
}

data class ContentModel(
    val title : String = "",
    val content : String = "",
    val uid : String = "",
    val time : String = ""
)