package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemAppendixBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class AppendixAdapter(private val appendixItems: List<AppendixItem>) : BaseBindingAdapter<ItemAppendixBinding>() {


    override fun getBinding(parent: ViewGroup): ItemAppendixBinding {
        return ItemAppendixBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = appendixItems.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemAppendixBinding>, position: Int) {
        val item = appendixItems[position]
        holder.binding.tvName.text = item.name
        holder.binding.root.setOnClickListener {
            item.onClick()
        }
    }

    data class AppendixItem(
        val name: String,
        val type: Int,
        val onClick: () -> Unit = {},
    )
}
