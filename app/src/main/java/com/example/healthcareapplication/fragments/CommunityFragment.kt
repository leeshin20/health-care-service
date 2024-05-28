package com.example.healthcareapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.healthcareapplication.DATA.ContentData
import com.example.healthcareapplication.DATA.ContentModel
import com.example.healthcareapplication.DATA.contentlist
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentCommunityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CommunityFragment : Fragment() {
    private lateinit var binding: FragmentCommunityBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val boardDataList = mutableListOf<ContentModel>()
    private val boardDataKey = mutableListOf<String>()
    private lateinit var adapter: contentlist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false)
        adapter = contentlist(boardDataList)
        binding.listview.adapter = adapter
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.writeBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_communityFragment_to_communityWriteFragment)
        }

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

        binding.listview.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = boardDataList[position]
            val bundle = Bundle().apply {
                putString("title", selectedItem.title)
                putString("content", selectedItem.content)
                putString("uid", selectedItem.uid)
                putString("time", selectedItem.time)
                putString("key", boardDataKey[position])
            }
            findNavController().navigate(R.id.action_communityFragment_to_contentclickFragment, bundle)
        }
        // getcontent 호출하여 데이터 로드
        getcontent()

        return view
    }

    private fun getcontent() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                boardDataList.clear()
                for (dataModel in datasnapshot.children) {
                    val item = dataModel.getValue(ContentModel::class.java)

                    if (item != null) {
                        boardDataList.add(item)
                        boardDataKey.add(dataModel.key.toString())
                        } else {
                        }
                }
                boardDataKey.reverse()
                boardDataList.reverse()
                adapter.notifyDataSetChanged()
                Log.d("test", "Adapter notified. Item count: ${adapter.count}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommunityFragment", "Failed to read data", error.toException())
            }
        }
        ContentData.boardRef.addValueEventListener(postListener)
    }
}
