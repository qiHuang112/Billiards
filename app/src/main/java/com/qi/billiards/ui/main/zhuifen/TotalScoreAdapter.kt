package com.qi.billiards.ui.main.zhuifen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.ui.util.dp2Px
import com.qi.billiards.ui.util.getScreenWidth

class TotalScoreAdapter(
    private val playerAndScoreList: List<ZhuiFenStartFragment.Companion.PlayerAndScore>,
    private var onItemClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<TotalScoreAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_total_score, parent, false)
        view.layoutParams.apply {
            val margin = playerAndScoreList.size * 2 * parent.context.dp2Px(5f)
            width = (parent.context.getScreenWidth() - margin) / playerAndScoreList.size
        }
        return ViewHolder(view)
    }

    override fun getItemCount() = playerAndScoreList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playerAndScore = playerAndScoreList[position]
        holder.tvName.text = playerAndScore.name
        holder.tvScore.text = playerAndScore.score
        if (playerAndScore.selected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_total_score_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_total_score_normal)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    fun setOnItemClickListener(listener: ((Int) -> Unit)?) {
        onItemClickListener = listener
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
