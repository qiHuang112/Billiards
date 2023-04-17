package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.game.Game
import com.qi.billiards.game.ZhuiFenGame

class GameBoardAdapter(
    val globalGame: ZhuiFenGame,
    val onOneGameClicked: ((Game) -> Unit)? = null
) : RecyclerView.Adapter<GameBoardAdapter.ViewHolder>() {

    var openDetail: MutableList<Boolean>

    init {
        openDetail = MutableList(globalGame.group.size) { true }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_game_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oneGame = globalGame.group[position]
        holder.tvOneGameSummary.text = getSummary(oneGame, position)
        holder.rvOneGameDetail.adapter = OneGameDetailAdapter(globalGame, position)
        holder.rvOneGameDetail.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemView.setOnClickListener {
            onOneGameClicked?.invoke(oneGame)
            notifyItemChanged(position)
        }

    }

    private fun getSummary(game: Game, position: Int): String {
        return "第${globalGame.group.size - position}局 ${game.sequences.joinToString()} 耗时：${game.during.getDuringTime()}"
    }

    override fun getItemCount() = globalGame.group.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvOneGameSummary: TextView
        var rvOneGameDetail: RecyclerView

        init {
            tvOneGameSummary = view.findViewById(R.id.tv_one_game_summary)
            rvOneGameDetail = view.findViewById(R.id.rv_one_game_detail)
        }
    }

}
