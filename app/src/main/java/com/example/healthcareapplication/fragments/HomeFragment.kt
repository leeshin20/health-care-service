package com.example.healthcareapplication.fragments
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
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
    private lateinit var containerLayout: ViewGroup
    private lateinit var containerLayout2: ViewGroup
    private var totalCalories = 0.0
    private var totalCarbs = 0.0
    private var totalProtein = 0.0
    private var totalSugars = 0.0
    private var totalSodium = 0.0
    private var metabolism = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        containerLayout = binding.listView
        containerLayout2 = binding.listView2
        binding.calender.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_calendarFragment)
        }
        binding.community.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_communityFragment)
        }
        binding.option.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_optionFragment)
            Log.d("option", "option1")
        }
        binding.statusBox.setOnClickListener{
            showDialog()
        }
        // button2 = 음식 검색 workoutBtn = 운동 추가
        binding.foodButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        binding.workoutButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_workoutFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val currentDate = SimpleDateFormat("M월 d일", Locale.getDefault()).format(Date())
        tvYear.text = currentYear
        tvDate.text = currentDate
        val dbdate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        fetchmetakcal()
        fetchData(dbdate)
        fetchNutri(dbdate)
        fetchworkData(dbdate)

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
                binding.eatKcal.setText("""
                    ${totalCalories}kcal
                """.trimIndent())
                fetchmetakcal()
            }

            override fun onCancelled(error: DatabaseError) {
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
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setTitle("음식 삭제")
                setMessage("${foodname}을(를) 삭제하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    deleteFoodFromFirebase(foodname)
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
                        fetchmetakcal()
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
    //운동 함수
    private fun fetchworkData(date: String) {
        val userId = auth.currentUser?.uid ?: return
        val workRef = database.child("users").child(userId).child("exercise").child(date)

        workRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Data snapshot received: ${snapshot.exists()}")

                for (childSnapshot in snapshot.children) {
                    val workName = childSnapshot.child("workname").getValue(String::class.java)
                    val repeat = childSnapshot.child("repeat").getValue(String::class.java)
                    val weight = childSnapshot.child("weight").getValue(String::class.java)
                    val workInfo = "운동명: $workName, 반복 횟수: $repeat, 무게: $weight kg"

                    if (!workName.isNullOrBlank()) {
                        addTextView2(workInfo, childSnapshot.key)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "데이터베이스 오류: ${error.message}")
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

        textView1.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setTitle("운동 삭제")
                setMessage("${workInfo}을(를) 삭제하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    deleteworkFromFirebase(workId)
                    containerLayout2.removeView(textView1)
                }
                setNegativeButton("아니오", null)
            }
            alertDialogBuilder.create().show()
        }
    }

    private fun deleteworkFromFirebase(workId: String?) {
        val userId = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        val workRef = database.child("users").child(userId).child("exercise").child(date).child(workId ?: return)

        workRef.removeValue().addOnSuccessListener {
            Toast.makeText(requireContext(), "운동 삭제 완료", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error ->
            Log.e(TAG, "데이터베이스 오류: ${error.message}")
            Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchmetakcal(){
        val userId = auth.currentUser?.uid ?: return
        val info = database.child("users").child(userId).child("userinfo")

        info.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val gender = snapshot.child("gender").getValue(String::class.java)
                    val height = snapshot.child("height").getValue(String::class.java)
                    val userweight = snapshot.child("weight").getValue(String::class.java)
                    val age = snapshot.child("age").getValue(String::class.java)
                    Log.d("gender", gender.toString())
                    Log.d("gender", height.toString())
                    Log.d("gender", userweight.toString())
                    Log.d("gender", age.toString())
                    if(gender == "남자"){
                        metabolism = calculateMetabolismForMale(height, userweight, age).toString()
                        binding.metabolismKcal.text = "${metabolism}kcal"
                    }
                    else if(gender == "여자"){
                        metabolism = calculateMetabolismForFemale(height, userweight, age).toString()
                        binding.metabolismKcal.text = "${metabolism}kcal"
                    }

                    val todayWeight = ((metabolism.toInt() - totalCalories)/7700)
                    val formattedWeight = String.format("%.3f", todayWeight)
                    binding.TodayWeight.text = "${formattedWeight}Kcal"

                }

            }

            override fun onCancelled(error: DatabaseError) {
                 Log.e(TAG, "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun calculateMetabolismForFemale(height: String?, weight: String?, age: String?): Int {
        val h = height?.toIntOrNull() ?: 0
        val w = weight?.toIntOrNull() ?: 0
        val a = age?.toIntOrNull() ?: 0

        // 간단한 예시 계산
        val metabolism = (655 + (9.6 * w) + (1.9 * h) - (4.7 * a)).toInt()

        return metabolism
    }
    private fun calculateMetabolismForMale(height: String?, weight: String?, age: String?): Int {
        val h = height?.toIntOrNull() ?: 0
        val w = weight?.toIntOrNull() ?: 0
        val a = age?.toIntOrNull() ?: 0

        val metabolism = (66 + (13.8 * w) + (5 * h) - (6.8 * a)).toInt()

        return metabolism
    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.userinfodialog, null)
        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("키/몸무게 수정")


        val alertDialog = mBuilder.show()

        val heightInput = mDialogView.findViewById<EditText>(R.id.height)
        val weightInput = mDialogView.findViewById<EditText>(R.id.weight)
        val maleRadioButton = mDialogView.findViewById<RadioButton>(R.id.rg_btn1)
        val okButton = mDialogView.findViewById<Button>(R.id.okbutton)
        val ageInput = mDialogView.findViewById<EditText>(R.id.age)
        okButton.setOnClickListener {
            val height = heightInput.text.toString()
            val weight = weightInput.text.toString()
            val age = ageInput.text.toString()
            val gender = if (maleRadioButton.isChecked) "남자" else "여자"

            saveUserInfoToFirebase(height, weight, gender, age)
            alertDialog.dismiss()
        }

        alertDialog.findViewById<Button>(R.id.cancelbutton)?.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun saveUserInfoToFirebase(height: String, weight: String, gender: String, age : String) {
        val userId = auth.currentUser?.uid ?: return
        val userInfo = mapOf(
            "height" to height,
            "weight" to weight,
            "gender" to gender,
            "age" to age
        )

        database.child("users").child(userId).child("userinfo").setValue(userInfo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "정보 저장 완료", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("HomeFragment", "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }

        fetchmetakcal()
    }

    }




















