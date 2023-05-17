package com.qi.billiards.ui.main.zhongba.start

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemOperatorGridBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.ui.main.UserOperator

class OperatorGridAdapter(
    private val userOperators: MutableList<UserOperator>,
    private val onUserOperate: ((Int) -> Unit)? = null
) : BaseBindingAdapter<ItemOperatorGridBinding>() {
    override fun getBinding(parent: ViewGroup): ItemOperatorGridBinding {
        return ItemOperatorGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemOperatorGridBinding>,
        position: Int
    ) {
        val userOperator = userOperators[position]
        holder.binding.tvDescription.text = userOperator.description
        userOperator.color?.let {
            holder.binding.tvDescription.setTextColor(Color.parseColor(userOperator.color))
        }
        holder.binding.root.setOnClickListener {
            onUserOperate?.invoke(position)
        }
    }

    override fun getItemCount() = userOperators.size


}
