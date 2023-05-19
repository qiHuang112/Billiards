package com.qi.billiards.ui.main

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemRuleMakingBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class RuleMakingAdapter(
    private val rules: List<RuleMakingItem>,
    private val click: (Int) -> Unit = {},
    private val longClick: () -> Unit = {},
) : BaseBindingAdapter<ItemRuleMakingBinding>() {


    override fun getBinding(parent: ViewGroup): ItemRuleMakingBinding {
        return ItemRuleMakingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = rules.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemRuleMakingBinding>, position: Int) {
        val rule = rules[position]
        if (rule.selectable) {
            holder.binding.cbSelect.visibility = View.VISIBLE
        } else {
            holder.binding.cbSelect.visibility = View.GONE
        }

        holder.binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            rule.isSelected = isChecked
        }
        holder.binding.tvContent.text = getSpannableText(rule)
        holder.binding.root.setOnClickListener {
            if (rule.selectable) {
                rule.isSelected = !rule.isSelected
                notifyItemChanged(position)
            } else {
                click(position)
            }
        }
        holder.binding.root.setOnLongClickListener {
            rule.isSelected = true
            longClick()
            true
        }
    }

    private fun getSpannableText(rule: RuleMakingItem): CharSequence {
        // 关键词为空，返回原文
        if (rule.keyword.isEmpty()) {
            return rule.content
        }
        // 没找到关键词，返回原文
        val startIndex = rule.content.indexOf(rule.keyword, ignoreCase = true)
        if (startIndex < 0) {
            return rule.content
        }
        return SpannableStringBuilder(rule.content).apply {
            setSpan(
                ForegroundColorSpan(Color.RED),
                startIndex,
                startIndex + rule.keyword.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    data class RuleMakingItem(
        var id: Long,
        val type: String,
        var content: String,
        var editTime: Long = System.currentTimeMillis(),
        var keyword: String = "",
        var selectable: Boolean = false,
        var isSelected: Boolean = false,
    )

}
