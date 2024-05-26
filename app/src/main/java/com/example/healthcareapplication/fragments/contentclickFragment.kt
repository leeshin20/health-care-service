package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentContentclickBinding

class contentclickFragment : Fragment() {
    private lateinit var binding: FragmentContentclickBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contentclick, container, false)
        val view = binding.root

        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        val uid = arguments?.getString("uid")
        val time = arguments?.getString("time")

        binding.contenttitle.text = title
        binding.contentcontent.text = content
        binding.contenttime.text = time
        return view
    }
}
