package com.qi.billiards.ui.main.zhuifen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.game.During
import com.qi.billiards.game.Player
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseFragment

class ZhuiFenFragment : BaseFragment() {

    private val ruleAdapter = RuleAdapter(getDefaultRules())
    private val playerAdapter = PlayerAdapter(getLatestPlayers())

    override fun getLayoutId() = R.layout.fragment_zhuifen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<TextView>(R.id.tv_start).setOnClickListener {
            val action =
                ZhuiFenFragmentDirections.actionZhuiFenFragmentToZhuiFenStartFragment(getZhuiFenGame())
            findNavController().navigate(action)
        }

        val rvCurrentRule = view.findViewById<RecyclerView>(R.id.rv_current_rule)
        rvCurrentRule.adapter = ruleAdapter
        rvCurrentRule.layoutManager = LinearLayoutManager(context)

        val rvCurrentPlayer = view.findViewById<RecyclerView>(R.id.rv_current_player)
        rvCurrentPlayer.adapter = playerAdapter
        rvCurrentPlayer.layoutManager = LinearLayoutManager(context)
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
            During()
        )
    }

    private fun getLatestPlayers(): MutableList<EditPlayer> {
        return mutableListOf(
            EditPlayer("黄麒"),
            EditPlayer("李逸波"),
            EditPlayer("辛宇"),
        )
    }

    companion object {
        private fun getDefaultRules(): List<EditRule> {
            return listOf(
                EditRule("初始分数", 25),
                EditRule("犯规罚分", 1),
                EditRule("普胜得分", 4),
                EditRule("小金得分", 7),
                EditRule("大金得分", 7),
                EditRule("基数", 5),
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