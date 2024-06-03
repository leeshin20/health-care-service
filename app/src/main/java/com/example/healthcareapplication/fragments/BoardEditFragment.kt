package com.example.healthcareapplication.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.healthcareapplication.R
import com.example.healthcareapplication.data.ContentData
import com.example.healthcareapplication.data.ContentModel
import com.example.healthcareapplication.databinding.FragmentBoardEditBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BoardEditFragment : Fragment() {
    private lateinit var binding: FragmentBoardEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var writerUid : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var contentkey = requireArguments().getString("contentment")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_edit, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.editBtn.setOnClickListener {
            val title = binding.title.text.toString()
            val content = binding.content.text.toString()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show()
            } else if(content.isBlank()){
                Toast.makeText(requireContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show()
            }else{
            editBoardData(contentkey.toString())
                findNavController().navigate(R.id.action_boardEditFragment_to_communityFragment)
            //fetchContent(title, content)
            }
        }

        binding.ImageBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }


        if (contentkey != null) {
            getBoardData(contentkey)
            getImageData(contentkey)
        }
        return view
    }

    private fun editBoardData(contentkey : String){
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)
        ContentData.boardRef
            .child(contentkey)
            .setValue(
                ContentModel(binding.title.text.toString(),
                binding.content.text.toString(),
                writerUid,
                dateFormat)
            ).addOnSuccessListener{
                ImageUpload(contentkey)
            }
    }
    private fun getBoardData(content: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                    val dataModel = snapshot.getValue(ContentModel::class.java)
                if(dataModel != null) {
                    binding.title.setText(dataModel.title)
                    binding.content.setText(dataModel.content)
                    writerUid = dataModel.uid
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        ContentData.boardRef.child(content).addValueEventListener(postListener)
    }private fun getImageData(key :String){
        val storageReference = Firebase.storage.reference.child(key + ".png")
        val imageViewFF = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task->
            if(task.isSuccessful){
                Glide.with(this).load(task.result).into(imageViewFF)
            } else{
            }
        })

    }
    private fun fetchContent(title: String, content: String) {
        val userId = auth.currentUser?.uid ?: return
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

        val key = ContentData.boardRef.push().key.toString()

        ContentData.boardRef
            .child(key)
            .setValue(ContentModel(title, content, userId, dateFormat))
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "게시글 업로드 성공!", Toast.LENGTH_SHORT).show()
                ImageUpload(key)
                findNavController().navigate(R.id.action_boardEditFragment_to_communityFragment)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ImageUpload(key: String) {
        val imageView = binding.imageArea
        if (imageView.drawable == null) {
            // 이미지가 없을 경우 업로드하지 않음
            return
        }

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            val selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                binding.imageArea.setImageURI(uri)
                val layoutParams = binding.imageArea.layoutParams
                layoutParams.width = 300 // 원하는 너비 설정
                layoutParams.height = 300 // 원하는 높이 설정
                binding.imageArea.layoutParams = layoutParams
            }
        }
    }
}
