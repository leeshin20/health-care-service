package com.example.healthcareapplication.DATA

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.healthcareapplication.R

class contentlist(val contentmodelList: MutableList<ContentModel>) : BaseAdapter() {
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
        var converView = convertView
        if(converView == null) {
            converView = LayoutInflater.from(parent?.context).inflate(R.layout.contentlistview, parent, false)
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