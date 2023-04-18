package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.ItemGameBoardBinding
import com.qi.billiards.game.Game
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingAdapter

class GameBoardAdapter(
    private val globalGame: ZhuiFenGame,
    private val onOneGameClicked: ((Game) -> Unit)? = null
) : BaseBindingAdapter<ItemGameBoardBinding>() {

    override fun getBinding(parent: ViewGroup): ItemGameBoardBinding {
        return ItemGameBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGameBoardBinding>, position: Int) {
        val oneGame = globalGame.group[position]
        holder.binding.tvOneGameSummary.text = getSummary(oneGame, position)
        holder.binding.rvOneGameDetail.adapter = OneGameDetailAdapter(globalGame, position)
        holder.binding.rvOneGameDetail.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemView.setOnClickListener {
            onOneGameClicked?.invoke(oneGame)
            notifyItemChanged(position)
        }

    }

    private fun getSummary(game: Game, position: Int): String {
        return "第${globalGame.group.size - position}局 ${game.sequences.joinToString()} 耗时：${game.during.getDuringTime()}"
    }

    override fun getItemCount() = globalGame.group.size

}
