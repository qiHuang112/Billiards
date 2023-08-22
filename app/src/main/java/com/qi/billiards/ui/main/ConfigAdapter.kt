package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.qi.billiards.databinding.ItemGameConfigBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.safeToInt

class ConfigAdapter(
    private val configs: LinkedHashMap<String, Double>,
) : BaseBindingAdapter<ItemGameConfigBinding>() {


    override fun getBinding(parent: ViewGroup): ItemGameConfigBinding {
        return ItemGameConfigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = configs.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGameConfigBinding>, position: Int) {
        val config = configs.toList()[position]
        holder.binding.tvName.text = config.first
        holder.binding.etInput.setText(config.second.toInt().toString())
        holder.binding.etInput.isEnabled = config.first != "误差筹码"
        holder.binding.etInput.doAfterTextChanged {
            configs[config.first] = it.toString().safeToInt().toDouble()
        }

    }

}
