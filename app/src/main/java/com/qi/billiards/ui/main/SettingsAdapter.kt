package com.qi.billiards.ui.main

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.qi.billiards.databinding.ItemSettingBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.get

class SettingsAdapter(private val items: List<SettingsItem>) : BaseBindingAdapter<ItemSettingBinding>() {
    override fun getBinding(parent: ViewGroup): ItemSettingBinding {
        return ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemSettingBinding>, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvName.text = item.name
            etInput.setText(item.config)
            etInput.hint = "请输入${item.name}"
            etInput.addTextChangedListener {
                item.config = it.toString()
                item.onConfigChanged(it.toString())
            }
        }
    }

    override fun getItemCount() = items.size

    data class SettingsItem(
        val name: String,
        var config: String,
        val onConfigChanged: (String) -> Unit = {}
    )
}
