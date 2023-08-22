package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemDevBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class DevAdapter(
    private val devItems: List<DevItem>
) : BaseBindingAdapter<ItemDevBinding>() {


    override fun getBinding(parent: ViewGroup): ItemDevBinding {
        return ItemDevBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemDevBinding>, position: Int) {
        val devItem = devItems[position]
        holder.binding.apply {
            tvName.text = devItem.name
            tvStatus.text = devItem.getStatusString()
            root.setOnClickListener {
                devItem.onClick(devItem.status)
            }
        }
    }

    override fun getItemCount() = devItems.size

    data class DevItem(
        val name: String,
        var status: Int = -1,
        val onClick: (Int) -> Unit = {}
    ) {
        fun getStatusString() = when (status) {
            0 -> "请求中"
            1 -> "成功"
            2 -> "失败"
            else -> ""
        }
    }

}
