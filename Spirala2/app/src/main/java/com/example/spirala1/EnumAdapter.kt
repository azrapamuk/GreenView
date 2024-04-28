package com.example.spirala1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class EnumAdapter(context: Context, private val values: List<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = values[position]
        return view
    }
}