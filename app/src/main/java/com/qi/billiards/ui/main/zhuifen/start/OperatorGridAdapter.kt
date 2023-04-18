package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemOperatorGridBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class OperatorGridAdapter(
    private val userOperators: MutableList<UserOperator>,
    private val onUserOperate: ((Int) -> Unit)? = null
) : BaseBindingAdapter<ItemOperatorGridBinding>() {
    override fun getBinding(parent: ViewGroup): ItemOperatorGridBinding {
        return ItemOperatorGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemOperatorGridBinding>, position: Int) {
        val userOperator = userOperators[position]
        holder.binding.tvDescription.text = userOperator.description
        holder.binding.root.setOnClickListener {
            onUserOperate?.invoke(position)
        }
    }

    override fun getItemCount() = userOperators.size

    data class UserOperator(
        val description: String,
        val operatorId: Int,
    )
}
