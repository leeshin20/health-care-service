package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healthcareapplication.R
import com.example.healthcareapplication.data.Userdata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchFragment : Fragment() {
    private lateinit var resultText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var uid = ""
    private var userdata: Userdata? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchInput: EditText = view.findViewById(R.id.search_input)
        resultText = view.findViewById(R.id.result_text1)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = currentUser.uid
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                resultText.text = ""
                val queryText = searchInput.text.toString().trim()
                if (queryText.isNotEmpty()) {
                    fetchNutritionInfo(queryText)
                }
                true
            } else false
        }

        resultText.setOnClickListener {
            saveDataToFirebase()
        }

        return view
    }

    private fun fetchNutritionInfo(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val keyId = "dc4a3f5b3d794a6fa0aa"
        val serviceId = "I2790"
        val dataType = "json"
        val startIdx = "1"
        val endIdx = "1"
        val url = "http://openapi.foodsafetykorea.go.kr/api/$keyId/$serviceId/$dataType/$startIdx/$endIdx/DESC_KOR=$encodedQuery"

        val requestQueue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                parseData(response, query)
            },
            { error ->
                resultText.text = "음식이름을 입력해주세요"
            })
        requestQueue.add(stringRequest)
    }

    private fun parseData(response: String, query: String) {
        try {
            val jsonObject = JSONObject(response)
            val items = jsonObject.getJSONObject("I2790").getJSONArray("row")
            var found = false
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val foodName = item.getString("DESC_KOR")
                if (foodName.contains(query, ignoreCase = true)) {
                    found = true
                    val calories = item.optString("NUTR_CONT1", "N/A")
                    val carbs = item.optString("NUTR_CONT2", "N/A")
                    val protein = item.optString("NUTR_CONT3", "N/A")
                    val sugars = item.optString("NUTR_CONT5", "N/A")
                    val sodium = item.optString("NUTR_CONT6", "N/A")

                    // Userdata 객체에 데이터를 저장
                    userdata = Userdata(
                        foodname = foodName,
                        calories = calories.toDoubleOrNull() ?: 0.0,
                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                        protein = protein.toDoubleOrNull() ?: 0.0,
                        sugars = sugars.toDoubleOrNull() ?: 0.0,
                        sodium = sodium.toDoubleOrNull() ?: 0.0
                    )

                    resultText.append("""
                        ${userdata?.foodname}:
                        칼로리: ${userdata?.calories} kcal
                        탄수화물: ${userdata?.carbs} g
                        단백질: ${userdata?.protein} g
                        당류: ${userdata?.sugars} g
                        나트륨: ${userdata?.sodium} mg
                    """.trimIndent())
                    resultText.append("\n\n")
                }
            }
            if (!found) {
                resultText.text = "올바른 음식이름을 입력해주세요"
            }
        } catch (e: Exception) {
            resultText.text = "올바른 식품이름를 입력해주세요. 시판 식품일수록 정확도가 올라갑니다."
        }
    }

    private fun saveDataToFirebase() {
        userdata?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.format(Date())

            database.child("users").child(uid).child("nutrition").child(date).push().setValue(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "음식 추가 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "데이터 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        } ?: run {
            Toast.makeText(requireContext(), "데이터 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }
}
