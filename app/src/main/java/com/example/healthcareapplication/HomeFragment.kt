package com.example.healthcareapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var listView: LinearLayout
    private var textViewCounter = 0
    //fragement
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        listView = rootView.findViewById(R.id.listView)

        val createTextBtn: Button = rootView.findViewById(R.id.button2)
        //SearchFragment로 화면 전환
        createTextBtn.setOnClickListener {
            createView()
        //val transaction = requireActivity().supportFragmentManager.beginTransaction()
            //transaction.replace(R.id.fragment_container, SearchFragment())
            //transaction.addToBackStack(null)
            //transaction.commit()
        }

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        tvDate.text = "$currentDate"
    }

    private fun createView() {
        val textViewNm = TextView(requireContext())
        textViewNm.text = "${textViewCounter++}"
        textViewNm.textSize = 12f
        textViewNm.setTypeface(null, Typeface.BOLD)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.marginStart = 30
        textViewNm.layoutParams = param
        textViewNm.setBackgroundColor(Color.rgb(184, 236, 184))
        textViewNm.setOnClickListener {
            // 각 텍스트 뷰를 클릭했을 때 실행할 동작을 여기에 작성합니다.
            dialog(textViewNm)
        }
        listView.addView(textViewNm)
    }
    private fun dialog(textView: TextView) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("삭제")
            setMessage("정말 삭제하시겠습니까?")
            setPositiveButton("OK") { dialog, which ->
                Toast.makeText(requireContext(), "OK Button Click", Toast.LENGTH_SHORT).show()
                textView?.let {
                    listView.removeView(it)
                }
            }
            setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(requireContext(), "Cancel Button Click", Toast.LENGTH_SHORT).show()
            }
            show()
        }
    }
}
