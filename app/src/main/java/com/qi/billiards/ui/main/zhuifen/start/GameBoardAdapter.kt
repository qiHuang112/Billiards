package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.game.OneGame
import com.qi.billiards.game.ZhuiFenGame

class GameBoardAdapter(
    val game: ZhuiFenGame,
    val onOneGameClicked: ((OneGame) -> Unit)? = null
) : RecyclerView.Adapter<GameBoardAdapter.ViewHolder>() {

    var openDetail: MutableList<Boolean>

    init {
        openDetail = MutableList(game.games.size) { true }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_game_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oneGame = game.games[position]
        holder.tvOneGameSummary.text = getSummary(oneGame, position)
        holder.rvOneGameDetail.adapter = OneGameDetailAdapter(game, position)
        holder.rvOneGameDetail.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemView.setOnClickListener {
            onOneGameClicked?.invoke(oneGame)
            openDetail[position] = !openDetail[position]
            notifyItemChanged(position)
        }

        holder.rvOneGameDetail.visibility = if (openDetail[position]) View.VISIBLE else View.GONE

    }

    private fun getSummary(oneGame: OneGame, position: Int): String {
        return "第${position + 1}局 顺序：${oneGame.sequences.joinToString()} 耗时：${oneGame.during}"
    }

    override fun getItemCount() = game.games.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvOneGameSummary: TextView
        var rvOneGameDetail: RecyclerView

        init {
            tvOneGameSummary = view.findViewById(R.id.tv_one_game_summary)
            rvOneGameDetail = view.findViewById(R.id.rv_one_game_detail)
        }
    }

}
