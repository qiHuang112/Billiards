package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.ItemGameBinding
import com.qi.billiards.db.GameEntity
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class GameEntityAdapter(
    private val games: MutableList<HistoryGame>,
    private val scope: CoroutineScope,
    private val onClickGame: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemGameBinding>() {

    override fun getBinding(parent: ViewGroup): ItemGameBinding {
        return ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGameBinding>, position: Int) {
        val game = games[position]
        scope.launch {
            val gameDetail = withContext(Dispatchers.IO) {
                Gson().fromJson(game.game.detail, ZhuiFenGame::class.java)
            }
            val players = gameDetail.players.sortedByDescending { it.name }
            val p1 = players.getOrNull(0)
            val p2 = players.getOrNull(1)
            val p3 = players.getOrNull(2)
            holder.binding.apply {
                tvBase.text = "x${gameDetail.base}"
                tvGameType.text = Config.gameType[game.game.gameType] ?: "未知"
                tvStartTime.text = game.game.startTime?.let { Date(it) }.format("yyyy年MM月dd日HH:mm:ss")
                tvP1.text = p1?.let { "${it.name}(${it.score})" }
                tvP2.text = p2?.let { "${it.name}(${it.score})" }
                tvP3.text = p3?.let { "${it.name}(${it.score})" }
                root.setOnClickListener {
                    onClickGame(position)
                }
            }
        }

    }

    override fun getItemCount() = games.size

    data class HistoryGame(
        var game: GameEntity,
    )
}
