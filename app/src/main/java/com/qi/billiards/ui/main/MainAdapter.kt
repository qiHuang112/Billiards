package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.R
import com.qi.billiards.databinding.ItemMainButtonBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class MainAdapter(
    private val mainItems: List<MainItem>
) : BaseBindingAdapter<ItemMainButtonBinding>() {

    override fun getBinding(parent: ViewGroup): ItemMainButtonBinding {
        return ItemMainButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemMainButtonBinding>,
        position: Int
    ) {
        val item = mainItems[position]
        holder.binding.root.setOnClickListener {
            item.onClick()
        }
        holder.binding.root.setOnLongClickListener {
            item.onLongClick()
            true
        }
        holder.binding.tvBtnName.text = item.name
        holder.binding.ivBtn.setImageResource(item.imageId)
        holder.binding.tvDesc.text = item.desc.joinToString("|")
    }

    override fun getItemCount() = mainItems.size

    data class MainItem(
        val name: String,
        val imageId: Int = R.drawable.heart,
        var desc: List<String> = arrayListOf(),
        val onLongClick: () -> Unit = {},
        val onClick: () -> Unit = {}
    )

}
