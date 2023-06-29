package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemPlayerBinding
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.ui.base.BaseBindingAdapter

class PlayerEntityAdapter(
    private val players: MutableList<PlayerEntity>,
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemPlayerBinding>() {
    override fun getBinding(parent: ViewGroup): ItemPlayerBinding {
        return ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPlayerBinding>, position: Int) {
        val player = players[position]
        holder.binding.apply {
            tvPlayerName.text = player.playerName
            tvTotalScore.text = player.totalScore.toString()
            tvWinCount.text = player.winCount.toString()
            tvTotalCount.text = player.totalCount.toString()
            tvWinRate.text = player.getWinRate()
            tvCost.text = String.format("%.1f", player.totalCost)
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }

    }

    override fun getItemCount() = players.size

}
