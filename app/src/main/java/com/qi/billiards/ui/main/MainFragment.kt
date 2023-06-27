package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config.dividerKey
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.game.DeGame
import com.qi.billiards.game.DePlayer
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.copyToClipboard
import com.qi.billiards.util.fromJson
import com.qi.billiards.util.toJson
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch

/**
 * 首页
 */
class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rvMain.adapter = MainAdapter(getMainItems())
        binding.rvMain.layoutManager = LinearLayoutManager(context)
    }

    private fun getMainItems(): List<MainAdapter.MainItem> {
        return listOf(
            MainAdapter.MainItem("德") {
                val action = MainFragmentDirections.actionToDe(
                    DeGame(mutableListOf(), DeFragment.getDeConfigs()), false
                )
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("历史记录") {
                val action = MainFragmentDirections.actionToHistory()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("玩家列表") {
                val action = MainFragmentDirections.actionToPlayer()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("导出历史", ::exportData),
            MainAdapter.MainItem("导入历史") {
                val action = MainFragmentDirections.actionToImport()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("修复数据", ::restore),
        )
    }

    private fun PlayerEntity.plusAssign(game: DeGame, dePlayer: DePlayer) {
        this.totalCount++
        this.totalCost += dePlayer.cost
        this.totalScore += (dePlayer.score - (dePlayer.buyCount + 1) * game.configs["单次买入"]!!.toInt()) / game.configs["汇率"]!!
        val win = if (dePlayer.profit > 0) 1 else 0
        this.winCount += win
    }

    private fun restore() {
        launch {
            val playerMap = mutableMapOf<String, PlayerEntity>()

            DbUtil.deleteAllPlayers()

            DbUtil.getAllGames().map {
                it.detail.fromJson<DeGame>()!!.apply {
                    players.map { dePlayer ->
                        val player = if (playerMap.contains(dePlayer.name)) {
                            playerMap[dePlayer.name]!!
                        } else {
                            val playerEntity = DeFragment.getPlayerEntity(dePlayer.name)
                            playerMap[dePlayer.name] = playerEntity
                            playerEntity
                        }
                        player.plusAssign(this, dePlayer)
                    }
                }
            }

            DbUtil.addPlayers(*playerMap.values.toTypedArray())
            toast("修复完成")

        }

    }


    private fun exportData() {
        launch {
            val data = "${DbUtil.getAllPlayers().toJson()}${dividerKey}${DbUtil.getAllGames().toJson()}"
            copyToClipboard(data)
            toast("本地数据已复制到剪切板")
        }
    }

}