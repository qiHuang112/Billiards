package com.qi.billiards.ui.main.zhuifen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.databinding.ItemCurrentPlayerBinding
import com.qi.billiards.ui.base.BaseBindingAdapter
import java.util.*

class PlayerAdapter(
    val editPlayers: MutableList<ZhuiFenFragment.Companion.EditPlayer>
) : BaseBindingAdapter<ItemCurrentPlayerBinding>() {

    override fun getBinding(parent: ViewGroup): ItemCurrentPlayerBinding {
        return ItemCurrentPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemCurrentPlayerBinding>,
        position: Int
    ) {
        val player = editPlayers[position]
        holder.binding.tvPlayerName.text = player.name

        holder.binding.ivUp.setOnClickListener {
            if (position > 0) {
                Collections.swap(editPlayers, position, position - 1)
                notifyDataSetChanged()
            }
        }

        holder.binding.ivDelete.setOnClickListener {
            editPlayers.removeAt(position)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount() = editPlayers.size
}
