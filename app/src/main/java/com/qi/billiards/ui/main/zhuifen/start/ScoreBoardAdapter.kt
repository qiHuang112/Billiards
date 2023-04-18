package com.qi.billiards.ui.main.zhuifen.start

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qi.billiards.R
import com.qi.billiards.databinding.ItemScoreBoardBinding
import com.qi.billiards.game.Player
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.util.dp2Px
import com.qi.billiards.util.getScreenWidth

class ScoreBoardAdapter(
    private val players: List<Player>,
    private var currentPlayerIndex: Int = -1,
    private val onClickUser: ((Int) -> Unit)? = null
) : BaseBindingAdapter<ItemScoreBoardBinding>() {

    override fun getBinding(parent: ViewGroup): ItemScoreBoardBinding {
        val binding =
            ItemScoreBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams.apply {
            width = (getScreenWidth() - dp2Px(5f) * 6) / players.size
        }
        return binding
    }

    override fun getItemCount() = players.size

    override fun onBindViewHolder(
        holder: BaseBindingViewHolder<ItemScoreBoardBinding>,
        position: Int
    ) {
        val player = players[position]
        holder.binding.tvName.text = player.name
        holder.binding.tvScore.text = player.score.toString()
        holder.binding.root.setOnClickListener {
            currentPlayerIndex = if (currentPlayerIndex == position) -1 else position
            onClickUser?.invoke(currentPlayerIndex)
            notifyDataSetChanged()
        }

        if (currentPlayerIndex == position) {
            holder.binding.root.setBackgroundResource(R.drawable.bg_total_score_selected)
        } else {
            holder.binding.root.setBackgroundResource(R.drawable.bg_total_score_normal)
        }
    }
}
