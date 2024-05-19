package com.example.healthcareapplication.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var containerLayout: ViewGroup // ViewGroup으로 변경하여 컨테이너 레이아웃 참조
    private var totalCalories = 0.0
    private var totalCarbs = 0.0
    private var totalProtein = 0.0
    private var totalSugars = 0.0
    private var totalSodium = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // DataBindingUtil을 사용하여 레이아웃을 inflate하고 binding 초기화
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        containerLayout = binding.listView // listView가 있는 LinearLayout 컨테이너로 가정

        // 네비게이션 버튼들에 대한 클릭 리스너 설정
        binding.calender.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_calendarFragment)
        }
        binding.community.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_communityFragment)
        }
        binding.option.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_optionFragment)
        }
        binding.button2.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        tvDate.text = currentDate

        // 현재 날짜의 데이터를 가져오기 위해 fetchData(currentDate) 호출
        fetchData(currentDate)
        fetchNutri(currentDate)
    }
    private fun fetchNutri(date: String){
        val userId = auth.currentUser?.uid ?: return
        val nutritionRef = database.child("users").child(userId).child("nutrition").child(date)
        nutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.rightText.text = ""
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
                binding.rightText.append("""
                        칼로리: ${totalCalories} kcal
                        탄수화물: ${totalCarbs} g
                        단백질: ${totalProtein} g
                        당류: ${totalSugars} g
                        나트륨: ${totalSodium} mg
                    """.trimIndent())
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터베이스 오류 처리
                Log.e(TAG, "데이터베이스 오류: ${error.message}")
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
                Log.e(TAG, "데이터베이스 오류: ${error.message}")
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

        textView.setOnClickListener {
            // AlertDialog를 이용하여 삭제 여부 확인
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setTitle("음식 삭제")
                setMessage("${foodname}을(를) 삭제하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    // 삭제 처리
                    deleteFoodFromFirebase(foodname)
                    // View에서도 삭제
                    containerLayout.removeView(textView)
                }
                setNegativeButton("아니오", null)
            }
            alertDialogBuilder.create().show()
        }
    }

    private fun deleteFoodFromFirebase(foodname: String) {
        val userId = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        val nutritionRef = database.child("users").child(userId).child("nutrition").child(date)

        nutritionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val name = childSnapshot.child("foodname").getValue(String::class.java)
                    if (name == foodname) {
                        childSnapshot.ref.removeValue() // Firebase에서 삭제
                        Toast.makeText(requireContext(), "$foodname 삭제 완료", Toast.LENGTH_SHORT).show()
                        fetchNutri(date)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
