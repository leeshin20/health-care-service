package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentCalendarBinding
import java.util.Locale

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        val view = binding.root

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        // val selectedDateText: TextView = view.findViewById(R.id.selected_date)

        binding.CalendarHomeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_calendarFragment_to_homeFragment)
        }
        binding.CalendarCommunity.setOnClickListener {
            it.findNavController().navigate(R.id.action_calendarFragment_to_communityFragment)
        }
        binding.CalendarOption.setOnClickListener {
            it.findNavController().navigate(R.id.action_calendarFragment_to_optionFragment)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
            val bundle = bundleOf("user_input" to date)
            findNavController().navigate(R.id.action_calendarFragment_to_recordFragment, bundle)
        }

        return view
    }
}
