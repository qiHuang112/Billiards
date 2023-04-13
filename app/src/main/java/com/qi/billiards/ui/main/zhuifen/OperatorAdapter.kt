package com.qi.billiards.ui.main.zhuifen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R

class OperatorAdapter(
    private val operators: MutableList<Operator>
) : RecyclerView.Adapter<OperatorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score_operator, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = operators.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val operator = operators[position]
        val text = operator.toString()
        if (text.isNotEmpty()) {
            holder.tvScoreText.text = text
        } else {
            holder.tvScoreText.visibility = View.GONE
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvScoreText: TextView

        init {
            tvScoreText = view.findViewById(R.id.tv_score_text)
        }
    }

    companion object {

        class Operator(
            val op: Int,
            val currentPlayersAndScore: List<ZhuiFenStartFragment.Companion.PlayerAndScore>
        ) {
            override fun toString(): String {
                return getDescriptionAndModifyTotalScore(this)
            }
        }

        private fun getDescriptionAndModifyTotalScore(operator: Operator): String {

            val playerAndScoreList = operator.currentPlayersAndScore

            val selectedIndex = playerAndScoreList.indexOfFirst { it.selected }
            if (selectedIndex == -1) return ""
            val size = playerAndScoreList.size

            val cur = playerAndScoreList[selectedIndex]
            val last = playerAndScoreList[(selectedIndex - 1 + size) % size]
            val next = playerAndScoreList[(selectedIndex + 1) % size]

            val description = when (operator.op) {
                ZhuiFenStartFragment.OP_FOUL -> {
                    "${cur.name} 自然犯规，扣分1分，${last.name} 得分1分"
                }
                ZhuiFenStartFragment.OP_FOUL_R -> {
                    "${cur.name} 解球犯规，扣分1分，${next.name} 得分1分"
                }
                ZhuiFenStartFragment.OP_WIN -> {
                    "${cur.name} 自然普胜，得分4分，${last.name} 扣分4分"
                }
                ZhuiFenStartFragment.OP_WIN_R -> {
                    "${cur.name} 解球普胜，得分4分，${next.name} 扣分4分"
                }
                ZhuiFenStartFragment.OP_XIAOJIN -> {
                    "${cur.name} 自然小金，得分7分，${last.name} 扣分7分"
                }
                ZhuiFenStartFragment.OP_XIAOJIN_R -> {
                    "${cur.name} 解球小金，得分7分，${next.name} 扣分7分"
                }
                ZhuiFenStartFragment.OP_DAJIN -> {
                    "${cur.name} 自然大金，得分${7 * size}分，其余人扣分7分"
                }
                else -> ""
            }

            return description
        }
    }

}
