package com.example.exovideoplayer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomSelectionAdapter(
    context: Context,
    options: Array<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, options) {

    private var selectedPosition = -1

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = super.getView(position, convertView, parent)
        val textView = itemView.findViewById<TextView>(android.R.id.text1)

        if (position == selectedPosition) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle, 0)
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        return itemView
    }
}
