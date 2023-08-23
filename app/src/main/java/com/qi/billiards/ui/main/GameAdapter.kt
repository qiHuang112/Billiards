package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.bean.Game
import com.qi.billiards.bean.Player
import com.qi.billiards.databinding.ItemHistoryGameBinding
import com.qi.billiards.databinding.ItemPlayerSummaryBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class GameAdapter(
    private val games: MutableList<Game>,
    val onClick: (Int) -> Unit
) : BaseBindingAdapter<ItemHistoryGameBinding>() {
    override fun getBinding(parent: ViewGroup): ItemHistoryGameBinding {
        return ItemHistoryGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemHistoryGameBinding>, position: Int) {
        val game = games[position]
        holder.binding.apply {
            tvGameName.text = buildString {
                append("[")
                append(games.size - position)
                append("]")
                append(":")
                append(game.date)
            }
            rvPlayerSummary.apply {
                adapter = PlayerSummaryAdapter(game.players)
            }
            root.setOnClickListener {
                onClick(position)
            }
        }
    }

    override fun getItemCount() = games.size


    class PlayerSummaryAdapter(private val players: List<Player>) : BaseBindingAdapter<ItemPlayerSummaryBinding>() {
        override fun getBinding(parent: ViewGroup): ItemPlayerSummaryBinding {
            return ItemPlayerSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPlayerSummaryBinding>, position: Int) {
            val player = players[position]
            holder.binding.apply {
                tvPlayerName.text = player.name
                tvPlayerProfit.text = player.profit.toString()
            }
        }

        override fun getItemCount() = players.size
    }
}
