package com.example.healthcareapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URLEncoder

class SearchFragment : Fragment() {
    private lateinit var resultText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchInput: EditText = view.findViewById(R.id.search_input)
        resultText = view.findViewById(R.id.result_text1)

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                resultText.text = ""  // Clear previous results
                val queryText = searchInput.text.toString()
                if (queryText.isNotEmpty()) {
                    fetchNutritionInfo(queryText)
                }
                true
            } else false
        }

        return view
    }

    private fun fetchNutritionInfo(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val keyId = "dc4a3f5b3d794a6fa0aa"
        val serviceId = "I2790"
        val dataType = "json"
        val startIdx = "1"
        val endIdx = "100"
        val url = "http://openapi.foodsafetykorea.go.kr/api/$keyId/$serviceId/$dataType/$startIdx/$endIdx/DESC_KOR=$encodedQuery"

        val requestQueue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                parseData(response, query)
            },
            { error ->
                resultText.text = "Error fetching data: ${error.localizedMessage}"
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

                    resultText.append("""
                        $foodName:
                        Calories: $calories kcal
                        Carbohydrates: $carbs g
                        Protein: $protein g
                        Sugars: $sugars g
                        Sodium: $sodium mg
                    """.trimIndent())
                    resultText.append("\n\n")
                }
            }
            if (!found) {
                resultText.text = "No results found for \"$query\"."
            }
        } catch (e: Exception) {
            resultText.text = "Failed to parse data: ${e}"
        }
    }
}