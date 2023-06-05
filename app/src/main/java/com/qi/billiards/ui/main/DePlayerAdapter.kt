package com.qi.billiards.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemDePlayerBinding
import com.qi.billiards.game.DePlayer
import com.qi.billiards.ui.base.BaseBindingAdapter

class DePlayerAdapter(
    private val dePlayers: MutableList<DePlayer>,
    private val onLongClick: (Int) -> Unit = {},
    private val onClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemDePlayerBinding>() {
    override fun getBinding(parent: ViewGroup): ItemDePlayerBinding {
        return ItemDePlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun getItemCount() = dePlayers.size

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemDePlayerBinding>, position: Int) {
        val player = dePlayers[position]
        holder.binding.tvDetail.text = getDetail(player)
        holder.binding.root.setOnClickListener {
            onClick(position)
        }
        holder.binding.root.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    private fun getDetail(player: DePlayer): CharSequence {
        return "${player.name} 买入${player.buyCount}次，剩余筹码${"%.0f".format(player.score)}，台费${"%.1f".format(player.cost)}，益损${player.profit}"
    }


}
