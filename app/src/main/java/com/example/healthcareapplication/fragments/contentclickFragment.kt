package com.example.healthcareapplication.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.healthcareapplication.DATA.ContentData
import com.example.healthcareapplication.DATA.ContentModel
import com.example.healthcareapplication.R
import com.example.healthcareapplication.databinding.FragmentContentclickBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class contentclickFragment : Fragment() {
    private lateinit var binding: FragmentContentclickBinding
    private lateinit var key: String
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contentclick, container, false)
        val view = binding.root

        binding.contentset.setOnClickListener {
            showDialog()
        }
        key = arguments?.getString("key").toString()
        getImageData(key.toString())
        Toast.makeText(requireContext(), key, Toast.LENGTH_LONG).show()
        getBoardData(key.toString())
        return view
    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")


        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            val bundle = bundleOf("contentment" to key)
            findNavController().navigate(R.id.action_contentclickFragment_to_boardEditFragment, bundle)
            alertDialog.dismiss()
        }
        alertDialog.findViewById<Button>(R.id.deleteBtn)?.setOnClickListener {
            ContentData.boardRef.child(key).removeValue()
            Toast.makeText(requireContext(), "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_contentclickFragment_to_communityFragment)
            alertDialog.dismiss()
        }
    }
    private fun getImageData(key :String){
        val storageReference = Firebase.storage.reference.child(key + ".png")
        val imageViewFF = binding.getImage

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task->
            if(task.isSuccessful){
                Glide.with(this).load(task.result).into(imageViewFF)
            } else{
            }
        })

    }
    private fun getBoardData(key : String){
        val userId = auth.currentUser?.uid ?: return
        val postListener = object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {

                val item = datasnapshot.getValue(ContentModel::class.java)

                if(item != null){
                    binding.contenttitle.text = item.title
                    binding.contentcontent.text = item.content
                    binding.contenttime.text = item.time

                    val writeUid = item.uid
                    Log.d("auth", auth.toString())
                    if(userId == writeUid){
                        binding.contentset.isVisible = true
                    } else{

                    }
                }

                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        ContentData.boardRef.child(key).addValueEventListener(postListener)
        }

    }
