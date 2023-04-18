package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemMainButtonBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class MainAdapter(
    private val mainItems: List<MainItem>
) : BaseBindingAdapter<ItemMainButtonBinding>() {

    override fun getBinding(parent: ViewGroup): ItemMainButtonBinding {
        return ItemMainButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemMainButtonBinding>, position: Int) {
        val item = mainItems[position]
        holder.binding.tvBtnName.text = item.name
        holder.binding.tvBtnName.setOnClickListener {
            item.onClick()
        }
    }

    override fun getItemCount() = mainItems.size

    data class MainItem(
        val name: String,
        val onClick: () -> Unit = {}
    )

}
