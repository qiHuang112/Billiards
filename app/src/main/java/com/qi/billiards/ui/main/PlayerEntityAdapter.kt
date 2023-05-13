package com.qi.billiards.ui.main

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemPlayerBinding
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.ui.base.BaseBindingAdapter

class PlayerEntityAdapter(
    private val players: MutableList<AddPlayer>,
    private val onLongClick: (Int) -> Unit = {}
) : BaseBindingAdapter<ItemPlayerBinding>() {
    override fun getBinding(parent: ViewGroup): ItemPlayerBinding {
        return ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPlayerBinding>, position: Int) {
        val player = players[position]
        holder.binding.apply {
            tvPlayerName.text = player.player.playerName
            tvId.text = player.player.id.toString()
            tvTotalScore.text = player.player.totalScore.toString()
            if (player.isNew) {
                player.isNew = false
                ObjectAnimator.ofArgb(root, "backgroundColor", Color.GREEN, Color.TRANSPARENT).apply {
                    duration = 1000
                    start()
                }
            }
            root.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }

    }

    override fun getItemCount() = players.size


    data class AddPlayer(
        val player: PlayerEntity,
        var isNew: Boolean = true
    )
}
