package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemImportBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class ImportAdapter(
    private val importItems: List<Pair<String, Int>>,
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemImportBinding>() {

    override fun getBinding(parent: ViewGroup): ItemImportBinding {
        return ItemImportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = importItems.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemImportBinding>, position: Int) {
        val importItem = importItems[position]
        holder.binding.apply {
            tvKey.text = buildString {
                append(importItem.first)
                append("（")
                if (importItem.second == 0) {
                    append("无更新")
                } else if (importItem.second > 0) {
                    append("有${importItem.second}条更新")
                } else {
                    append("有${-importItem.second}条需要上传")
                }
                append("）")
            }
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }
    }

}
