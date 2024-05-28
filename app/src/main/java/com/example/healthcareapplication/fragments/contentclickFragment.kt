package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.healthcareapplication.DATA.ContentData
import com.example.healthcareapplication.DATA.ContentModel
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentContentclickBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class contentclickFragment : Fragment() {
    private lateinit var binding: FragmentContentclickBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contentclick, container, false)
        val view = binding.root

//        val title = arguments?.getString("title")
//        val content = arguments?.getString("content")
//        val uid = arguments?.getString("uid")
//        val time = arguments?.getString("time")
//
//        binding.contenttitle.text = title
//        binding.contentcontent.text = content
//        binding.contenttime.text = time

        val key = arguments?.getString("key")
        Toast.makeText(requireContext(), key, Toast.LENGTH_LONG).show()
        getBoardData(key.toString())
        return view
    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                val item = datasnapshot.getValue(ContentModel::class.java)

                if(item != null){
                    binding.contenttitle.text = item.title
                    binding.contentcontent.text = item.content
                    binding.contenttime.text = item.time
                }

                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        ContentData.boardRef.child(key).addValueEventListener(postListener)
        }

    }
