package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemCurrentRuleBinding
import com.qi.billiards.game.EditRule
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.safeToInt

class RuleAdapter(
    val rules: List<EditRule>
) : BaseBindingAdapter<ItemCurrentRuleBinding>() {

    override fun getBinding(parent: ViewGroup): ItemCurrentRuleBinding {
        return ItemCurrentRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = rules.size

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemCurrentRuleBinding>,
        position: Int
    ) {
        val rule = rules[position]
        holder.binding.tvRuleName.text = rule.name

        holder.binding.etNumber.setText("${rule.score}")
        holder.binding.etNumber.setSelection("${rule.score}".length)
        holder.binding.ivAdd.setOnClickListener {
            rule.score = holder.binding.etNumber.text.toString().safeToInt() + 1
            notifyItemChanged(position)
        }
        holder.binding.ivMinus.setOnClickListener {
            rule.score = holder.binding.etNumber.text.toString().safeToInt() - 1
            notifyItemChanged(position)
        }
    }

}
