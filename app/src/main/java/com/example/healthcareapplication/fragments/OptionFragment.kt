package com.example.healthcareapplication.fragments

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.healthcareapplication.R
import com.example.healthcareapplication.aboutlog.LoadingActivity
import com.example.healthcareapplication.databinding.FragmentOptionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option, container, false)
        val view = binding.root

        binding.signout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoadingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        binding.description.setOnClickListener {
            showDialog1()
        }

        binding.release.setOnClickListener {
            showDialog2()
        }

        binding.edit.setOnClickListener {
            showDialog3()
        }
        binding.OptionHomeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_homeFragment)
            Log.d("tag", "home")
        }
        binding.OptionCalender.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_calendarFragment)
            Log.d("tag", "community")
        }
        binding.OptionCommunity.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_communityFragment)
            Log.d("tag", "option")
        }

        return view
    }

    private fun showDialog1() {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.description, null)
        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)


        val alertDialog = mBuilder.show()
        val okbutton = mDialogView.findViewById<Button>(R.id.okbtn)
        okbutton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showDialog2() {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.releasenote, null)
        val mBuilder = android.app.AlertDialog.Builder(context)
            .setView(mDialogView)


        val alertDialog = mBuilder.show()
        val okbutton = mDialogView.findViewById<Button>(R.id.okbtn)
        okbutton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showDialog3() {
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

    private fun saveUserInfoToFirebase(
        height: String,
        weight: String,
        gender: String,
        age: String
    ) {
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
    }

}
