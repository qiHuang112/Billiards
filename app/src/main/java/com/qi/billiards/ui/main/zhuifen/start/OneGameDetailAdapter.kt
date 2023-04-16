package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.game.ZhuiFenGame

class OneGameDetailAdapter(
    val game: ZhuiFenGame,
    val gamePosition: Int
) : RecyclerView.Adapter<OneGameDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_one_game_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDetail.text = getDetail(position)
    }

    private fun getDetail(position: Int): String {
        val oneGame = game.games[gamePosition]
        val operator = oneGame.operators[position]
        val sequence = oneGame.sequences
        val opPlayer = operator.player.name
        // qitodo
        return ""
    }

    override fun getItemCount() = game.games[gamePosition].operators.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDetail: TextView

        init {
            tvDetail = view.findViewById(R.id.tv_detail)
        }
    }

}
