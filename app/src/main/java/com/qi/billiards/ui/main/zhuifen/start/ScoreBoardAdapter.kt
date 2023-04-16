package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.game.Player
import com.qi.billiards.util.dp2Px
import com.qi.billiards.util.getScreenWidth

class ScoreBoardAdapter(
    val players: List<Player>,
    val onClickUser: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder>() {

    var currentPlayerIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_score_board, parent, false)
        view.layoutParams.apply {
            width = (context.getScreenWidth() - context.dp2Px(5f) * 6) / players.size
        }
        return ViewHolder(view)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.tvName.text = player.name
        holder.tvScore.text = player.score.toString()
        holder.itemView.setOnClickListener {
            currentPlayerIndex = if (currentPlayerIndex == position) -1 else position
            onClickUser?.invoke(currentPlayerIndex)
            notifyDataSetChanged()
        }

        if (currentPlayerIndex == position) {
            holder.itemView.setBackgroundResource(R.drawable.bg_total_score_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_total_score_normal)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var tvScore: TextView

        init {
            tvName = view.findViewById(R.id.tv_name)
            tvScore = view.findViewById(R.id.tv_score)
        }
    }
}
