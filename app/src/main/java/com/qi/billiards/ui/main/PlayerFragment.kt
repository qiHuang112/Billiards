package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.R
import com.qi.billiards.databinding.FragmentPlayerBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.get
import com.qi.billiards.util.save
import kotlinx.coroutines.launch

/**
 * 玩家列表
 */
class PlayerFragment : BaseBindingFragment<FragmentPlayerBinding>() {

    private val players = mutableListOf<PlayerEntity>()
    private val playerAdapter = PlayerEntityAdapter(players)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        binding.rgSort.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_win_rate -> players.sortBy {
                    -it.getWinRate().removeSuffix("%").toDouble()
                }
                R.id.rb_profits -> players.sortBy {
                    -it.totalScore
                }

                else -> players.sortBy {
                    it.id
                }
            }
            save(KEY_LAST_SORTED_METHOD, checkedId)
            playerAdapter.notifyItemRangeChanged(0, players.size)
        }

        binding.rvPlayerEntity.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        launch {
            DbUtil.getAllPlayers().let(players::addAll)
            playerAdapter.notifyItemRangeChanged(0, players.size)
            binding.rgSort.check(get(KEY_LAST_SORTED_METHOD, R.id.rb_default))
        }

    }

    companion object {
        const val KEY_LAST_SORTED_METHOD = "KEY_LAST_SORTED_METHOD"
    }
}