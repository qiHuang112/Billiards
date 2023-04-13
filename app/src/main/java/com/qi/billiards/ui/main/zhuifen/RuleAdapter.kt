package com.qi.billiards.ui.main.zhuifen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.ui.util.DefaultConfig
import com.qi.billiards.ui.util.safeToInt

class RuleAdapter(
    private val rules: List<ZhuiFenFragment.Companion.Rule>
) : RecyclerView.Adapter<RuleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_current_rule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = rules.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rules[position]
        holder.tvName.text = rule.name

        holder.etNumber.setText(rule.score)
        holder.etNumber.setSelection(rule.score.length)
        holder.ivAdd.setOnClickListener {
            rule.score = "${holder.etNumber.toString().safeToInt() + 1}"
            notifyItemChanged(position)
        }
        holder.ivMinus.setOnClickListener {
            rule.score = "${holder.etNumber.toString().safeToInt() - 1}"
            notifyItemChanged(position)
        }
        holder.tvReset.setOnClickListener {
            rule.score = DefaultConfig.ZhuiFen.get(rule.name)
            notifyItemChanged(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var ivAdd: ImageView
        var ivMinus: ImageView
        var etNumber: EditText
        var tvReset: TextView

        init {
            tvName = view.findViewById(R.id.tv_rule_name)
            ivAdd = view.findViewById(R.id.iv_add)
            ivMinus = view.findViewById(R.id.iv_minus)
            etNumber = view.findViewById(R.id.et_number)
            tvReset = view.findViewById(R.id.tv_rule_reset)
        }
    }
}
