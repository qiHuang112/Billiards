package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.bean.GlobalPlayer
import com.qi.billiards.databinding.ItemPlayerBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class GlobalPlayerAdapter(
    private val key: String,
    private val players: MutableList<GlobalPlayer>,
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemPlayerBinding>() {
    override fun getBinding(parent: ViewGroup): ItemPlayerBinding {
        return ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPlayerBinding>, position: Int) {
        val player = players[position]
        holder.binding.apply {
            tvPlayerName.text = player.name
            tvTotalScore.text = player.totalProfit.toString()
            tvWinCount.text = player.winCount.toString()
            tvTotalCount.text = player.totalCount.toString()
            tvWinRate.text = player.getWinRate()
            tvLast5.text = player.getLast5Profit(key)
            tvCost.text = String.format("%.1f", player.totalCost)
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }

    }

    override fun getItemCount() = players.size

}
