package com.example.healthcareapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONException
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {
    private lateinit var resultText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchInput: EditText = findViewById(R.id.search_input)
        resultText = findViewById(R.id.result_text)

        // 키보드의 검색 버튼을 누를 때 이벤트 처리
        searchInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                resultText.text = ""  // 이전 검색 결과를 지웁니다.
                fetchNutritionInfo(v.text.toString())
                true
            } else false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchNutritionInfo(query: String) {
        val keyId = "dc4a3f5b3d794a6fa0aa"
        val serviceId = "I2790"
        val dataType = "json"
        val startIdx = "1"
        val endIdx = "100"
        val url = "http://openapi.foodsafetykorea.go.kr/api/$keyId/$serviceId/$dataType/$startIdx/$endIdx"

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    parseData(response, resultText, query)
                } catch (e: Exception) {
                    resultText.text = "Parsing error: ${e.localizedMessage}"
                    Log.e("API Error", "Parsing Error: ", e)
                }
            },
            { error ->
                resultText.text = "Error: $error"
                Log.e("API Error", "Network error: ", error)
            })
        requestQueue.add(stringRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun parseData(response: String, resultText: TextView, query: String) {
        try {
            val jsonObject = JSONObject(response)
            val items = jsonObject.getJSONObject("result").getJSONArray("items")
            var found = false
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val foodName = item.getString("DESC_KOR")
                if (foodName.contains(query, ignoreCase = true)) {
                    found = true
                    val calories = item.optString("NUTR_CONT1", "N/A") // 열량
                    val carbs = item.optString("NUTR_CONT2", "N/A")    // 탄수화물
                    val protein = item.optString("NUTR_CONT3", "N/A")  // 단백질
                    val sugars = item.optString("NUTR_CONT5", "N/A")   // 당류
                    val sodium = item.optString("NUTR_CONT6", "N/A")   // 나트륨

                    resultText.append(
                        """
                    $foodName:
                    Calories: $calories kcal
                    Carbohydrates: $carbs g
                    Protein: $protein g
                    Sugars: $sugars g
                    Sodium: $sodium mg
                    """.trimIndent()
                    )
                    resultText.append("\n\n")
                }
            }
            if (!found) {
                resultText.text = "No results found for \"$query\""
            }
        } catch (e: JSONException) {
            resultText.text = "Failed to parse data: ${e.localizedMessage}"
            e.printStackTrace()
        }
    }
}

