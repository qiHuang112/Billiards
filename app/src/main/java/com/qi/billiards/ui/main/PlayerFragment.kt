package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.R
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentPlayerBinding
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.get
import com.qi.billiards.util.save

/**
 * 玩家列表
 */
class PlayerFragment(val key: String) : BaseBindingFragment<FragmentPlayerBinding>() {

    private val players = AppData.globalPlayer[key]?.toList()?.map { it.second }?.toMutableList() ?: mutableListOf()
    private val playerAdapter = GlobalPlayerAdapter(key, players)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        binding.rgSort.apply {
            setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_win_rate -> players.sortBy {
                        -it.getWinRate().removeSuffix("%").toDouble()
                    }
                    R.id.rb_profits -> players.sortBy {
                        -it.totalProfit
                    }

                    else -> players.sortBy {
                        -it.getLast5Profit(key).toFloat()
                    }
                }
                save(KEY_LAST_SORTED_METHOD + key, checkedId)
                playerAdapter.notifyItemRangeChanged(0, players.size)
            }
            check(get(KEY_LAST_SORTED_METHOD + key, R.id.rb_default))
        }

        binding.rvPlayerEntity.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    companion object {
        const val KEY_LAST_SORTED_METHOD = "KEY_LAST_SORTED_METHOD_"
    }
}