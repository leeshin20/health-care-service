package com.example.healthcareapplication.DATA

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.healthcareapplication.R
import com.google.firebase.auth.FirebaseAuth

class contentlist(val contentmodelList: MutableList<ContentModel>) : BaseAdapter() {
    private lateinit var auth: FirebaseAuth
    override fun getCount(): Int {
        return contentmodelList.size
    }

    override fun getItem(position: Int): Any {
        return contentmodelList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        var converView = convertView
            converView = LayoutInflater.from(parent?.context).inflate(R.layout.contentlistview, parent, false)
        val itemLinearView = converView?.findViewById<LinearLayout>(R.id.itemView)

        if(contentmodelList[position].uid == userId){
            itemLinearView?.setBackgroundColor(Color.parseColor("#ffa500"))
        }

        val title = converView?.findViewById<TextView>(R.id.title)
        title!!.text = contentmodelList[position].title

        val content = converView?.findViewById<TextView>(R.id.content)
        content!!.text = contentmodelList[position].content

        val time = converView?.findViewById<TextView>(R.id.time)
        time!!.text = contentmodelList[position].time


        return converView!!
    }
}