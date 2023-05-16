package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.qi.billiards.databinding.FragmentHistoryBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingFragment
import kotlinx.coroutines.launch

class HistoryFragment : BaseBindingFragment<FragmentHistoryBinding>() {

    private val games = mutableListOf<GameEntityAdapter.HistoryGame>()
    private val gameAdapter = GameEntityAdapter(games, ::jumpToDetail)
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

        launch {
            DbUtil.getAllGames().map { GameEntityAdapter.HistoryGame(it) }.let(games::addAll)
            gameAdapter.notifyItemRangeChanged(0, games.size)
        }
    }

    override fun onCustomResume() {
        if (clickedPos >= 0) {
            launch {
                val changedGame = DbUtil.getGameById(games[clickedPos].game.gameId!!)
                if (changedGame != null) {
                    games[clickedPos].game = changedGame
                    gameAdapter.notifyItemChanged(clickedPos)
                }
                clickedPos = -1
            }
        }
    }

    private fun jumpToDetail(position: Int) {

        clickedPos = position
        val game = games[position]
        val action = HistoryFragmentDirections.actionToZhuiFen(
            Gson().fromJson(game.game.detail, ZhuiFenGame::class.java), true
        )
        findNavController().navigate(action)
    }
}