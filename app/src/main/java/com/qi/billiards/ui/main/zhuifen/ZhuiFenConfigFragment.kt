package com.qi.billiards.ui.main.zhuifen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.FragmentZhuifenConfigBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.During
import com.qi.billiards.game.Player
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.LinkedHashMap

class ZhuiFenConfigFragment : BaseBindingFragment<FragmentZhuifenConfigBinding>() {

    private val players = mutableListOf<EditPlayer>()

    private val ruleAdapter = RuleAdapter(getDefaultRules())
    private val playerAdapter = PlayerAdapter(players)
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentZhuifenConfigBinding {
        return FragmentZhuifenConfigBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvStart.setOnClickListener {
            val action =
                ZhuiFenConfigFragmentDirections.actionZhuiFenConfigFragmentToZhuiFenFragment(
                    getZhuiFenGame(),
                    false
                )
            findNavController().navigate(action)
        }

        binding.rvCurrentRule.adapter = ruleAdapter
        binding.rvCurrentRule.layoutManager = LinearLayoutManager(context)

        binding.rvCurrentPlayer.adapter = playerAdapter
        binding.rvCurrentPlayer.layoutManager = LinearLayoutManager(context)

        launch {
            val dbPlayers = withContext(Dispatchers.IO) {
                DbUtil.getAllPlayers().map {
                    EditPlayer(it.playerName)
                }
            }
            players.addAll(dbPlayers)
            playerAdapter.notifyDataSetChanged()
        }
    }

    private fun getZhuiFenGame(): ZhuiFenGame {
        val editRules = ruleAdapter.rule
        val rule = ZhuiFenGame.Rule(
            editRules.find { it.name == "犯规罚分" }?.score ?: 1,
            editRules.find { it.name == "普胜得分" }?.score ?: 4,
            editRules.find { it.name == "小金得分" }?.score ?: 7,
            editRules.find { it.name == "大金得分" }?.score ?: 7,
        )
        return ZhuiFenGame(
            mutableListOf(),
            playerAdapter.editPlayers.map { ep ->
                Player(ep.name, editRules.find { it.name == "初始分数" }?.score ?: 100)
            },
            rule,
            editRules.find { it.name == "基数" }?.score ?: 5,
            During(Date()),
            LinkedHashMap<String, MutableMap<String, Int>>().apply {
                playerAdapter.editPlayers.forEach {
                    put(it.name, mutableMapOf())
                }
            }
        )
    }

    companion object {
        private fun getDefaultRules(): List<EditRule> {
            return listOf(
                EditRule("初始分数", 0),
                EditRule("犯规罚分", 1),
                EditRule("普胜得分", 4),
                EditRule("小金得分", 7),
                EditRule("大金得分", 7),
                EditRule("基数", 1),
            )
        }

        data class EditRule(
            var name: String,
            var score: Int,
        )

        data class EditPlayer(
            var name: String
        )
    }
}