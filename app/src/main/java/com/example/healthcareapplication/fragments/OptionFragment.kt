package com.example.healthcareapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.healthcareapplication.R
import com.example.healthcareapplication.aboutlog.LoadingActivity
import com.example.healthcareapplication.databinding.FragmentOptionBinding
import com.google.firebase.auth.FirebaseAuth

class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option, container, false)
        val view = binding.root

        binding.signout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoadingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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
}
