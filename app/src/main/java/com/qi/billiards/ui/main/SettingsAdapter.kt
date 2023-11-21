package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemSettingsBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.save

class SettingsAdapter(
    private val settings: List<Setting>
) : BaseBindingAdapter<ItemSettingsBinding>() {
    override fun getBinding(parent: ViewGroup): ItemSettingsBinding {
        return ItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = settings.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemSettingsBinding>, position: Int) {
        val setting = settings[position]
        holder.binding.apply {
            tvName.text = setting.name
            tbSwitch.isChecked = setting.onOff
            tbSwitch.setOnClickListener {
                setting.onOff = !setting.onOff
                tbSwitch.isChecked = setting.onOff
                save(SETTINGS_KEY_ + setting.name, setting.onOff)
            }
        }
    }

}

const val SETTINGS_KEY_ = "SETTINGS_KEY_"
const val yybb = "语音播报"

data class Setting(
    val name: String,
    var onOff: Boolean,
)