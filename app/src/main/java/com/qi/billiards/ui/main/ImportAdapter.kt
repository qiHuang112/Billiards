package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemImportBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.format
import java.util.Date

class ImportAdapter(
    private val importItems: MutableList<ImportItem>,
    private val onClick: (Int) -> Unit = {},
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemImportBinding>() {

    override fun getBinding(parent: ViewGroup): ItemImportBinding {
        return ItemImportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = importItems.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemImportBinding>, position: Int) {
        val importItem = importItems[position]
        holder.binding.apply {
            tvKey.text = importItem.key
            tvTime.text = Date(importItem.createTime).format("yyyy/MM/dd HH:mm:ss")
            root.setOnClickListener {
                onClick(position)
            }
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }
    }

    data class ImportItem(
        var key: String,
        var content: String? = null,
        var createTime: Long = System.currentTimeMillis()
    ) : java.io.Serializable

}
