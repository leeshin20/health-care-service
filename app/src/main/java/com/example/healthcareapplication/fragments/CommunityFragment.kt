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
import com.example.healthcareapplication.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {
    private lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false)
        val view = binding.root

        binding.Cohometap.setOnClickListener {
            it.findNavController().navigate(R.id.action_communityFragment_to_homeFragment)
            Log.d("tag", "home")
        }
        binding.Cocalender.setOnClickListener {
            it.findNavController().navigate(R.id.action_communityFragment_to_calendarFragment)
            Log.d("tag", "community")
        }
        binding.Cooption.setOnClickListener {
            it.findNavController().navigate(R.id.action_communityFragment_to_optionFragment)
            Log.d("tag", "option")
        }

        return view
    }
}
