package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.ItemGameBinding
import com.qi.billiards.db.GameEntity
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.format
import java.util.Date

class GameEntityAdapter(
    private val games: MutableList<HistoryGame>,
    private val onClickGame: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemGameBinding>() {

    override fun getBinding(parent: ViewGroup): ItemGameBinding {
        return ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGameBinding>, position: Int) {
        val game = games[position]
        holder.binding.apply {
            tvId.text = game.game.gameId.toString()
            tvGameType.text = Config.gameType[game.game.gameType] ?: "未知"
            tvStartTime.text = game.game.startTime?.let { Date(it) }.format("yyyy年MM月dd日")
            root.setOnClickListener {
                onClickGame(position)
            }
        }
    }

    override fun getItemCount() = games.size

    data class HistoryGame(
        var game: GameEntity,
    )
}
