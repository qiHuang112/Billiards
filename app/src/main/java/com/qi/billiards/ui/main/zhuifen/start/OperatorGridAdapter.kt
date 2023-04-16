package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R

class OperatorGridAdapter(
    val userOperators: MutableList<UserOperator>,
    val onUserOperate: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<OperatorGridAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_operator_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userOperator = userOperators[position]
        holder.tvDescription.text = userOperator.description
        holder.itemView.setOnClickListener {
            onUserOperate?.invoke(position)
        }
    }

    override fun getItemCount() = userOperators.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDescription:TextView
        init {
            tvDescription = view.findViewById(R.id.tv_description)
        }
    }

    data class UserOperator(
        val description: String,
        val operatorId: Int,
    )
}
