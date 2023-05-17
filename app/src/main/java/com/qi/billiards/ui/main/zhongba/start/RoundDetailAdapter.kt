package com.qi.billiards.ui.main.zhongba.start

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.ItemRoundDetailBinding
import com.qi.billiards.game.ZhongBaGame
import com.qi.billiards.ui.base.BaseBindingAdapter

class RoundDetailAdapter(
    val game: ZhongBaGame,
    private val gamePosition: Int
) : BaseBindingAdapter<ItemRoundDetailBinding>() {

    override fun getBinding(parent: ViewGroup): ItemRoundDetailBinding {
        return ItemRoundDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemRoundDetailBinding>,
        position: Int
    ) {
        holder.binding.tvDetail.text = getDetail(position)
        holder.binding.tvDetail.setTextColor(getColor(position))
    }

    private fun getColor(position: Int): Int {
        val opId = game.group[gamePosition].operators[position].id
        return Color.parseColor(Config.ZhongBa.opColorMap.getOrDefault(opId, "#000000"))
    }

    /**
     * 拿到一局游戏中，某个操作的文字描述
     *
     * example:%s%s，扣分%d分，%s得分%d分
     */
    private fun getDetail(position: Int): String {
        val round = game.group[gamePosition] // 这一局游戏
        val operator = round.operators[position] // 操作
        val sequence = round.sequences // 顺序表
        val opPlayer = operator.player.name // 操作玩家
        val rule = game.rule
        val curIndex = sequence.indexOf(opPlayer)
        if (curIndex == -1) return ""
        val last = if (curIndex == 0) sequence.last() else sequence[curIndex - 1] // 上家
        val next = if (curIndex == sequence.lastIndex) sequence.first() else sequence[curIndex + 1] // 下家
        val curDes = "${opPlayer}${Config.ZhongBa.opMap.getOrDefault(operator.id, "[未定义操作]")}，"
        return when (operator.id) {
            Config.ZhongBa.OP_0 -> {

                "${curDes}得分${rule.win}分，${last}扣分${rule.win}分"
            }

            Config.ZhongBa.OP_1 -> {

                "${curDes}得分${rule.zhaqing}分，${next}扣分${rule.zhaqing}分"
            }

            Config.ZhongBa.OP_2 -> {

                "${curDes}得分${rule.jieqing}分，${last}扣分${rule.jieqing}分"
            }

            else -> "未定义的操作类型[${operator.id}]"
        }
    }

    override fun getItemCount() = game.group[gamePosition].operators.size

}
