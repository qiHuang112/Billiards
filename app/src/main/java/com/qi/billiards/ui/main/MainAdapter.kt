package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R

class MainAdapter(private val mainItems: List<MainItem>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_main_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mainItems[position]
        holder.tvBtnName.text = item.name
        holder.tvBtnName.setOnClickListener {
            item.onClick()
        }
    }

    override fun getItemCount() = mainItems.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvBtnName: TextView

        init {
            tvBtnName = view.findViewById(R.id.tv_btn_name)
        }
    }

    data class MainItem(
        val name: String,
        val onClick: () -> Unit = {}
    )

}
