package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.bean.Player
import com.qi.billiards.databinding.ItemGamePlayerBinding
import com.qi.billiards.ui.base.BaseBindingAdapter

class PlayerAdapter(
    private val players: MutableList<Player>,
    private val onLongClick: (Int) -> Unit = {},
    private val onClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemGamePlayerBinding>() {
    override fun getBinding(parent: ViewGroup): ItemGamePlayerBinding {
        return ItemGamePlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemGamePlayerBinding>, position: Int) {
        val player = players[position]
        holder.binding.tvDetail.text = getDetail(player)
        holder.binding.root.setOnClickListener {
            onClick(position)
        }
        holder.binding.root.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    private fun getDetail(player: Player): CharSequence {
        return "${player.name} 买入${player.buyCount}次，剩余筹码${"%.0f".format(player.score)}，台费${"%.1f".format(player.cost)}，益损${player.profit}"
    }


}
