package com.example.healthcareapplication.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentRecordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecordFragment : Fragment() {
private lateinit var binding : FragmentRecordBinding
    private lateinit var database: DatabaseReference
    private lateinit var containerLayout: ViewGroup
    private lateinit var containerLayout2: ViewGroup
    private lateinit var auth: FirebaseAuth
    private var totalCalories = 0.0
    private var totalCarbs = 0.0
    private var totalProtein = 0.0
    private var totalSugars = 0.0
    private var totalSodium = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var date = requireArguments().getString("user_input")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        containerLayout = binding.firstTextView // containerLayout 초기화
        containerLayout2 = binding.thirdTextView // containerLayout2 초기화
        if (date != null) {
            fetchData(date)
            fetchNutri(date)
            fetchworkData(date)
        } else{
            Toast.makeText(requireContext(), "날짜 오류!!", Toast.LENGTH_SHORT).show()
        }
        val view = binding.root
        return view
    }
    private fun fetchNutri(date: String){
        val userId = auth.currentUser?.uid ?: return
        val nutritionRef = database.child("users").child(userId).child("nutrition").child(date)
        nutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.secondTextView.text = ""
                totalCalories = 0.0
                totalCarbs = 0.0
                totalProtein = 0.0
                totalSugars = 0.0
                totalSodium = 0.0

                for (childSnapshot in snapshot.children) {
                    val calories = childSnapshot.child("calories").getValue(Int::class.java) ?: 0
                    val carbs = childSnapshot.child("carbs").getValue(Int::class.java) ?: 0
                    val protein = childSnapshot.child("protein").getValue(Int::class.java) ?: 0
                    val sodium = childSnapshot.child("sodium").getValue(Int::class.java) ?: 0
                    val sugars = childSnapshot.child("sugars").getValue(Int::class.java) ?: 0

                    totalCalories += calories
                    totalCarbs += carbs
                    totalProtein += protein
                    totalSodium += sodium
                    totalSugars += sugars
                }
                binding.secondTextView.append("""
                        칼로리: ${totalCalories} kcal
                        탄수화물: ${totalCarbs} g
                        단백질: ${totalProtein} g
                        당류: ${totalSugars} g
                        나트륨: ${totalSodium} mg
                    """.trimIndent())
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 오류 처리
                Log.e(ContentValues.TAG, "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fetchData(date: String) {
        val userId = auth.currentUser?.uid ?: return
        val nutritionRef = database.child("users").child(userId).child("nutrition").child(date)

        nutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val foodname = childSnapshot.child("foodname").getValue(String::class.java)
                    if (!foodname.isNullOrBlank()) {
                        addTextView(foodname)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 오류 처리
                Log.e(ContentValues.TAG, "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTextView(foodname: String) {
        val textView = TextView(requireContext())
        textView.text = foodname
        textView.textSize = 18f
        textView.setPadding(16, 16, 16, 16)
        containerLayout.addView(textView)
    }


    //운동 함수
    private fun fetchworkData(date: String) {
        val userId = auth.currentUser?.uid ?: return
        val workRef = database.child("users").child(userId).child("exercise").child(date)

        Log.d(ContentValues.TAG, "Fetching work data for date: $date") // 함수가 호출되었는지 확인하는 로그

        workRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "Data snapshot received: ${snapshot.exists()}") // 데이터가 존재하는지 확인하는 로그

                for (childSnapshot in snapshot.children) {
                    val workName = childSnapshot.child("workname").getValue(String::class.java)
                    val repeat = childSnapshot.child("repeat").getValue(String::class.java)
                    val weight = childSnapshot.child("weight").getValue(String::class.java)
                    val workInfo = "운동명: $workName, 반복 횟수: $repeat, 무게: $weight kg"

                    Log.d(ContentValues.TAG, "Work fetched: $workInfo") // 각 운동 데이터를 확인하는 로그

                    if (!workName.isNullOrBlank()) {
                        addTextView2(workInfo, childSnapshot.key) // 키를 전달하여 삭제 시 참조
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 오류 처리
                Log.e(ContentValues.TAG, "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTextView2(workInfo: String, workId: String?) {
        val textView1 = TextView(requireContext())
        textView1.text = workInfo
        textView1.textSize = 18f
        textView1.setPadding(16, 16, 16, 16)
        containerLayout2.addView(textView1)

    }



    }