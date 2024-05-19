package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.healthcareapplication.DATA.workout
import com.example.healthcareapplication.databinding.FragmentWorkoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var workoutContainer: LinearLayout // LinearLayout으로 변경하여 ScrollView에 추가할 뷰 그룹을 참조
    private var uid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = currentUser.uid
        }

        workoutContainer = binding.workoutContainer // ScrollView의 LinearLayout을 참조

        binding.plusBtn.setOnClickListener {
            val workoutName = binding.workname.text.toString().trim()
            val repetitionsStr = binding.workrepeat.text.toString().trim()
            val weightStr = binding.weight.text.toString().trim()

            if (workoutName.isNotEmpty() && repetitionsStr.isNotEmpty() && weightStr.isNotEmpty()) {
                val repetitions = repetitionsStr.toIntOrNull()
                val weight = weightStr.toDoubleOrNull()

                if (repetitions != null && weight != null) {
                    val workout = workout(workoutName, repetitionsStr, weightStr)
                    saveDataToFirebase(workout)
                } else {
                    Toast.makeText(context, "Invalid input for repetitions or weight", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter all values", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        fetchWorkoutsForDate(currentDate)
    }

    private fun saveDataToFirebase(workout: workout) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        database.child("users").child(uid).child("exercise").child(date).push().setValue(workout)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
                    // 저장 성공 시 UI 갱신
                    fetchWorkoutsForDate(date)
                } else {
                    Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchWorkoutsForDate(date: String) {
        val userId = auth.currentUser?.uid ?: return
        val exerciseRef = database.child("users").child(userId).child("exercise").child(date)

        exerciseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutContainer.removeAllViews() // 기존에 추가된 뷰 모두 삭제

                for (childSnapshot in snapshot.children) {
                    val workout = childSnapshot.getValue(workout::class.java)
                    workout?.let {
                        addWorkoutTextView(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("WorkoutFragment", "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addWorkoutTextView(workout: workout) {
        val workoutInfo = "운동명: ${workout.workname}, 반복: ${workout.repeat}, 무게: ${workout.weight}"

        val textView = TextView(requireContext())
        textView.text = workoutInfo
        textView.textSize = 18f
        textView.setPadding(16, 16, 16, 16)
        workoutContainer.addView(textView)

        textView.setOnClickListener {
            showDeleteConfirmationDialog(workout)
        }
    }

    private fun showDeleteConfirmationDialog(workout: workout) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle("운동 삭제")
            setMessage("${workout.workname}을(를) 삭제하시겠습니까?")
            setPositiveButton("예") { _, _ ->
                deleteWorkoutFromFirebase(workout)
            }
            setNegativeButton("아니오", null)
        }
        alertDialogBuilder.create().show()
    }

    private fun deleteWorkoutFromFirebase(workout: workout) {
        val userId = auth.currentUser?.uid ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        val exerciseRef = database.child("users").child(userId).child("exercise").child(date)

        exerciseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val dbWorkout = childSnapshot.getValue(workout::class.java)
                    if (dbWorkout?.workname == workout.workname &&
                        dbWorkout.repeat == workout.repeat &&
                        dbWorkout.weight == workout.weight) {
                        childSnapshot.ref.removeValue()
                        Toast.makeText(requireContext(), "${workout.workname} 삭제 완료", Toast.LENGTH_SHORT).show()
                        // 삭제 후 UI 갱신
                        fetchWorkoutsForDate(date)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("WorkoutFragment", "데이터베이스 오류: ${error.message}")
                Toast.makeText(requireContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
