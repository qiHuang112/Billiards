package com.qi.billiards.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.bean.Game
import com.qi.billiards.bean.Player
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentHistoryBinding
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.dp
import com.qi.billiards.util.getBooleanByDialog
import com.qi.billiards.util.hideSystemKeyboard
import com.qi.billiards.util.safeAs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 历史对局
 */
class HistoryFragment(val key: String) : BaseBindingFragment<FragmentHistoryBinding>() {

    private val games = getHistoryGames()
    private val gameAdapter = GameAdapter(games, ::jumpToDetail, ::onClickPlayer, ::onLongClick)
    private var clickedPos = -1
    private var searchJob: Job? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        binding.etSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = searchText(it?.trim().toString())
        }

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

    private fun onClickPlayer(player: Player) {
        if (binding.etSearch.isVisible) {
            binding.etSearch.setText(player.name)
        }
    }

    private fun jumpToDetail(position: Int) {

        clickedPos = position
        val action = HistoryFrameFragmentDirections.actionToGame(
            games[position].game, true
        )
        findNavController().navigate(action)
    }

    private fun onLongClick(game: Game) {
        launch {
            if (getBooleanByDialog("确定要删除${game.type}[${game.date}]吗？")) {
                AppData.deleteGame(game)
                games.removeIf { it.game == game }
                gameAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchText(keyword: String) = launch {
        delay(200)
        val searchResult = getHistoryGames().filter { game ->
            game.game.players.any { it.name.contains(keyword) }
        }.map { it.setKeyword(keyword) }
        games.clear()
        games.addAll(searchResult)
        binding.rvGameEntity.adapter?.notifyDataSetChanged()
    }

    private fun getHistoryGames(): MutableList<GameAdapter.HistoryGame> {
        return AppData.globalGames[key]
            ?.reversed()
            ?.map { GameAdapter.HistoryGame(it) }
            ?.toMutableList()
            ?: mutableListOf()
    }

    fun showOrHideSearchView() {
        if (destroyed) {
            return
        }
        val isShow = binding.etSearch.visibility == View.VISIBLE
        if (isShow) {
            val animatorHide = ValueAnimator.ofFloat(50.dp, 0.dp)
            animatorHide.addUpdateListener {
                if (!destroyed) {
                    binding.etSearch.translationY = it.animatedValue as Float
                    binding.rvGameEntity.translationY = it.animatedValue as Float
                }
            }
            animatorHide.addListener(onEnd = {
                if (!destroyed) {
                    binding.etSearch.visibility = View.GONE
                    binding.rvGameEntity.layoutParams.safeAs<MarginLayoutParams>()?.let {
                        it.bottomMargin = 0.dp.toInt()
                    }
                    hideSystemKeyboard(binding.etSearch)
                }
            })
            animatorHide.duration = 200
            animatorHide.start()
        } else {
            val animatorShow = ValueAnimator.ofFloat(0.dp, 50.dp)
            animatorShow.addUpdateListener {
                if (!destroyed) {
                    binding.etSearch.translationY = it.animatedValue as Float
                    binding.rvGameEntity.translationY = it.animatedValue as Float
                }
            }
            animatorShow.addListener(onEnd = {
                if (!destroyed) {
                    binding.etSearch.visibility = View.VISIBLE
                    binding.rvGameEntity.layoutParams.safeAs<MarginLayoutParams>()?.let {
                        it.bottomMargin = 50.dp.toInt()
                    }
                }
            })
            animatorShow.duration = 200
            animatorShow.start()
        }

    }
}