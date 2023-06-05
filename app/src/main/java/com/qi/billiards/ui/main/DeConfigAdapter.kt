package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.qi.billiards.databinding.ItemDeConfigBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.safeToInt

class DeConfigAdapter(
    private val deConfigs: LinkedHashMap<String, Double>,
) : BaseBindingAdapter<ItemDeConfigBinding>() {


    override fun getBinding(parent: ViewGroup): ItemDeConfigBinding {
        return ItemDeConfigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = deConfigs.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemDeConfigBinding>, position: Int) {
        val config = deConfigs.toList()[position]
        holder.binding.tvName.text = config.first
        holder.binding.etInput.setText(config.second.toInt().toString())
        holder.binding.etInput.isEnabled = config.first != "误差筹码"
        holder.binding.etInput.doAfterTextChanged {
            deConfigs[config.first] = it.toString().safeToInt().toDouble()
        }

    }

}
