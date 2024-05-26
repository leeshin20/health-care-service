package com.example.healthcareapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.healthcareapplication.DATA.ContentData
import com.example.healthcareapplication.DATA.ContentModel
import com.example.healthcareapplication.DATA.Userdata
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentCommunityWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CommunityWriteFragment : Fragment() {
    private lateinit var binding: FragmentCommunityWriteBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community_write, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()


        binding.writeBtn.setOnClickListener {
            val title = binding.title.text.toString()
            val content = binding.content.text.toString()

            fetchContent(title, content)

        }





        return view
    }

    private fun fetchContent(title:String, content:String){
        val userId = auth.currentUser?.uid ?: return
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

        ContentData.boardRef
            .push()
            .setValue(ContentModel(title, content, userId, dateFormat))
        Toast.makeText(requireContext(), "게시글 업로드 성공!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_communityWriteFragment_to_communityFragment)

    }
}