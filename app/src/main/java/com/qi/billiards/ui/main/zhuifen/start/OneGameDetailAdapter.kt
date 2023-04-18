package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.ItemOneGameDetailBinding
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingAdapter

class OneGameDetailAdapter(
    val game: ZhuiFenGame,
    private val gamePosition: Int
) : BaseBindingAdapter<ItemOneGameDetailBinding>() {

    override fun getBinding(parent: ViewGroup): ItemOneGameDetailBinding {
        return ItemOneGameDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemOneGameDetailBinding>, position: Int) {
        holder.binding.tvDetail.text = getDetail(position)
    }

    /**
     * 拿到一局游戏中，某个操作的文字描述
     *
     * example:%s%s，扣分%d分，%s得分%d分
     */
    private fun getDetail(position: Int): String {
        val oneGame = game.group[gamePosition] // 这一局游戏
        val operator = oneGame.operators[position] // 操作
        val sequence = oneGame.sequences // 顺序表
        val opPlayer = operator.player.name // 操作玩家
        val rule = game.rule
        val curIndex = sequence.indexOf(opPlayer)
        if (curIndex == -1) return ""
        val last = if (curIndex == 0) sequence.last() else sequence[curIndex - 1] // 上家
        val next =
            if (curIndex == sequence.lastIndex) sequence.first() else sequence[curIndex + 1] // 下家
        val curDes = "${opPlayer}${Config.ZhuiFen.opMap.getOrDefault(operator.id, "[未定义操作]")}，"
        return when (operator.id) {
            Config.ZhuiFen.OP_0 -> {
                "${curDes}扣分${rule.foul}分，${last}得分${rule.foul}分"
            }

            Config.ZhuiFen.OP_1 -> {

                "${curDes}扣分${rule.foul}分，${next}得分${rule.foul}分"
            }

            Config.ZhuiFen.OP_2 -> {

                "${curDes}得分${rule.win}分，${last}扣分${rule.win}分"
            }

            Config.ZhuiFen.OP_3 -> {

                "${curDes}得分${rule.win}分，${next}扣分${rule.win}分"
            }

            Config.ZhuiFen.OP_4 -> {

                "${curDes}得分${rule.xiaojin}分，${last}扣分${rule.xiaojin}分"
            }

            Config.ZhuiFen.OP_5 -> {

                "${curDes}得分${rule.xiaojin}分，${next}扣分${rule.xiaojin}分"
            }

            Config.ZhuiFen.OP_6 -> {

                "${curDes}得分${rule.dajin * sequence.lastIndex}分，其余玩家扣分${rule.dajin}分"
            }

            else -> "未定义的操作类型[${operator.id}]"
        }
    }

    override fun getItemCount() = game.group[gamePosition].operators.size

}
