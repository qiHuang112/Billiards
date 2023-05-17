package com.qi.billiards.ui.main.zhongba.start

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.ItemGameBoardBinding
import com.qi.billiards.game.Round
import com.qi.billiards.game.ZhongBaGame
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.format

class GameBoardAdapter(
    private val game: ZhongBaGame,
    private val onRoundClicked: ((Round) -> Unit)? = null
) : BaseBindingAdapter<ItemGameBoardBinding>() {

    override fun getBinding(parent: ViewGroup): ItemGameBoardBinding {
        return ItemGameBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGameBoardBinding>, position: Int) {
        val round = game.group[position]
        holder.binding.tvRoundSummary.text = getSummary(round, position)
        holder.binding.rvRoundDetail.adapter = RoundDetailAdapter(game, position)
        holder.binding.rvRoundDetail.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemView.setOnClickListener {
            onRoundClicked?.invoke(round)
            notifyItemChanged(position)
        }

    }

    private fun getSummary(round: Round, position: Int): String {
        return "${round.during.startTime.format()} 第${game.group.size - position}局 ${round.sequences.joinToString()} ${round.during.getCostTime()}"
    }

    override fun getItemCount() = game.group.size

}
