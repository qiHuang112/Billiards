package com.qi.billiards.ui.main.zhuifen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import java.util.*

class PlayerAdapter(
    val editPlayers: MutableList<ZhuiFenFragment.Companion.EditPlayer>
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_current_player, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = editPlayers[position]
        holder.tvName.text = player.name

        holder.ivUp.setOnClickListener {
            if (position > 0) {
                Collections.swap(editPlayers, position, position - 1)
                notifyDataSetChanged()
            }
        }

        holder.ivDelete.setOnClickListener {
            editPlayers.removeAt(position)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount() = editPlayers.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var ivUp: ImageView
        var ivDelete: ImageView

        init {
            tvName = view.findViewById(R.id.tv_player_name)
            ivUp = view.findViewById(R.id.iv_up)
            ivDelete = view.findViewById(R.id.iv_delete)

        }
    }

}
