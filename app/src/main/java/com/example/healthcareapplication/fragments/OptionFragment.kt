package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentOptionBinding

class OptionFragment : Fragment() {
    private lateinit var binding: FragmentOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option, container, false)
        val view = binding.root

        binding.Ohometap.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_homeFragment)
            Log.d("tag", "home")
        }
        binding.Ocalender.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_calendarFragment)
            Log.d("tag", "community")
        }
        binding.Ocommunity.setOnClickListener {
            it.findNavController().navigate(R.id.action_optionFragment_to_communityFragment)
            Log.d("tag", "option")
        }

        return view
    }
}
