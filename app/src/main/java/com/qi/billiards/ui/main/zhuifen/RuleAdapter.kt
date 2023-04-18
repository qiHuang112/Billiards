package com.qi.billiards.ui.main.zhuifen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.ItemCurrentRuleBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.safeToInt

class RuleAdapter(
    val rule: List<ZhuiFenFragment.Companion.EditRule>
) : BaseBindingAdapter<ItemCurrentRuleBinding>() {

    override fun getBinding(parent: ViewGroup): ItemCurrentRuleBinding {
        return ItemCurrentRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = rule.size

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemCurrentRuleBinding>,
        position: Int
    ) {
        val rule = rule[position]
        holder.binding.tvRuleName.text = rule.name

        holder.binding.etNumber.setText("${rule.score}")
        holder.binding.etNumber.setSelection("${rule.score}".length)
        holder.binding.etNumber.doAfterTextChanged {
            rule.score = holder.binding.etNumber.text.toString().safeToInt()
        }
        holder.binding.ivAdd.setOnClickListener {
            rule.score = holder.binding.etNumber.text.toString().safeToInt() + 1
            notifyItemChanged(position)
        }
        holder.binding.ivMinus.setOnClickListener {
            rule.score = holder.binding.etNumber.text.toString().safeToInt() - 1
            notifyItemChanged(position)
        }
        holder.binding.tvRuleReset.setOnClickListener {
            rule.score = Config.ZhuiFen.get(rule.name)
            notifyItemChanged(position)
        }
    }

}
