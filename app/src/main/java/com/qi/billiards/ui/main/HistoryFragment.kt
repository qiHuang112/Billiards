package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentHistoryBinding
import com.qi.billiards.ui.base.BaseBindingFragment

/**
 * 历史对局
 */
class HistoryFragment(val key: String) : BaseBindingFragment<FragmentHistoryBinding>() {

    private val games = AppData.globalGames[key]?.reversed()?.toMutableList() ?: mutableListOf()
    private val gameAdapter = GameAdapter(games, ::jumpToDetail)
    private var clickedPos = -1

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.rvGameEntity.apply {
            adapter = gameAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCustomResume() {
        if (clickedPos >= 0) {
            gameAdapter.notifyItemChanged(clickedPos)
            clickedPos = -1
        }
    }

    private fun jumpToDetail(position: Int) {

        clickedPos = position
        val action = HistoryFrameFragmentDirections.actionToGame(
            games[position], true
        )
        findNavController().navigate(action)
    }
}