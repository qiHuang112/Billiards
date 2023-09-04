package com.qi.billiards.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.bean.Game
import com.qi.billiards.bean.Player
import com.qi.billiards.databinding.ItemHistoryGameBinding
import com.qi.billiards.databinding.ItemPlayerSummaryBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class GameAdapter(
    private val games: MutableList<HistoryGame>,
    val onClick: (Int) -> Unit,
    val onClickPlayer: (Player) -> Unit,
    val onLongClick: (Game) -> Unit = {}
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
                append(game.game.date)
            }
            rvPlayerSummary.adapter = PlayerSummaryAdapter(game.keyword, game.game.players) {
                onClickPlayer(it)
            }

            root.setOnClickListener {
                onClick(position)
            }
            root.setOnLongClickListener {
                onLongClick(game.game)
                true
            }
        }
    }

    override fun getItemCount() = games.size


    class PlayerSummaryAdapter(
        private val keyword: String,
        private val players: List<Player>,
        private val onClickPlayer: (Player) -> Unit
    ) : BaseBindingAdapter<ItemPlayerSummaryBinding>() {
        override fun getBinding(parent: ViewGroup): ItemPlayerSummaryBinding {
            return ItemPlayerSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPlayerSummaryBinding>, position: Int) {
            val player = players[position]
            holder.binding.apply {
                tvPlayerName.text = player.name
                tvPlayerProfit.text = player.profit.toString()
                if (keyword.isNotEmpty() && player.name.contains(keyword, true)) {
                    tvPlayerName.setTextColor(Color.RED)
                    tvPlayerProfit.setTextColor(Color.RED)
                } else {
                    tvPlayerName.setTextColor(Color.BLACK)
                    tvPlayerProfit.setTextColor(Color.BLACK)
                }
                root.setOnClickListener {
                    onClickPlayer(player)
                }
            }
        }

        override fun getItemCount() = players.size
    }

    data class HistoryGame(
        val game: Game,
        var keyword: String = "",
    ) {
        fun setKeyword(keyword: String): HistoryGame {
            this.keyword = keyword
            return this
        }
    }
}
