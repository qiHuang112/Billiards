package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemImportBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class ImportAdapter(
    private val importItems: List<String>,
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemImportBinding>() {

    override fun getBinding(parent: ViewGroup): ItemImportBinding {
        return ItemImportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = importItems.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemImportBinding>, position: Int) {
        val importItem = importItems[position]
        holder.binding.apply {
            tvKey.text = importItem
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }
    }

}
