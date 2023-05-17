package com.qi.billiards.ui.main.zhongba

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.FragmentZhongbaConfigBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.EditPlayer
import com.qi.billiards.game.Player
import com.qi.billiards.game.ZhongBaGame
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.ui.main.PlayerAdapter
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

class ZhongBaConfigFragment : BaseBindingFragment<FragmentZhongbaConfigBinding>() {

    private val players = mutableListOf<EditPlayer>()

    private val playerAdapter = PlayerAdapter(players)
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentZhongbaConfigBinding {
        return FragmentZhongbaConfigBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvStart.setOnClickListener {
            val action = ZhongBaConfigFragmentDirections.actionToZhongBa(getZhongBaGame(), false)
            findNavController().navigate(action)
        }

        binding.tvNewPlayer.setOnClickListener {
            val action = ZhongBaConfigFragmentDirections.actionToNewPlayer()
            findNavController().navigate(action)
        }

        binding.rvCurrentPlayer.adapter = playerAdapter
        binding.rvCurrentPlayer.layoutManager = LinearLayoutManager(context)

    }

    override fun onCustomResume() {
        launch {
            val dbPlayers = DbUtil.getAllPlayers().map {
                EditPlayer(it.playerName, it.id!!)
            }
            players.clear()
            players.addAll(dbPlayers)
            playerAdapter.notifyItemRangeChanged(0, players.size)
        }
    }

    private fun getZhongBaGame(): ZhongBaGame {

        return ZhongBaGame(
            mutableListOf(),
            playerAdapter.editPlayers.map { ep ->
                Player(ep.name, 0, ep.id)
            },
            LinkedHashMap<String, MutableMap<String, Int>>().apply {
                playerAdapter.editPlayers.forEach {
                    put(it.name, mutableMapOf())
                }
            }
        )
    }

}