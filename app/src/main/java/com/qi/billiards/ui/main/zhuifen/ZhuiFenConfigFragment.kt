package com.qi.billiards.ui.main.zhuifen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.FragmentZhuifenConfigBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.During
import com.qi.billiards.game.Player
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.get
import com.qi.billiards.util.safeToInt
import com.qi.billiards.util.save
import kotlinx.coroutines.launch
import java.util.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvStart.setOnClickListener {
            val action =
                ZhuiFenConfigFragmentDirections.actionToZhuiFen(
                    getZhuiFenGame(),
                    false
                )
            findNavController().navigate(action)
        }

        binding.tvNewPlayer.setOnClickListener {
            val action = ZhuiFenConfigFragmentDirections.actionToNewPlayer()
            findNavController().navigate(action)
        }

        binding.rvCurrentRule.adapter = ruleAdapter
        binding.rvCurrentRule.layoutManager = LinearLayoutManager(context)

        binding.rvCurrentPlayer.adapter = playerAdapter
        binding.rvCurrentPlayer.layoutManager = LinearLayoutManager(context)

    }

    override fun onCustomResume() {
        launch {
            val dbPlayers = DbUtil.getAllPlayers().map {
                EditPlayer(it.playerName)
            }
            players.clear()
            players.addAll(dbPlayers)
            playerAdapter.notifyItemRangeChanged(0, players.size)
        }
    }

    private fun getZhuiFenGame(): ZhuiFenGame {
        val editRules = ruleAdapter.rules
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

    override fun onCustomPause() {
        saveDefaultRules(ruleAdapter.rules)
    }

    companion object {

        private const val KEY_RULES = "KEY_RULES"
        private const val DEFAULT_RULES = "0|1|4|7|7|1"

        private fun getDefaultRules(): List<EditRule> {
            return get(KEY_RULES, "").ifEmpty { DEFAULT_RULES }.split("|").mapIndexed { index, score ->
                EditRule(Config.ZhuiFen.ruleString[index], score.safeToInt())
            }
        }

        private fun saveDefaultRules(rules: List<EditRule>) {
            save(KEY_RULES, rules.joinToString("|") { it.score.toString() })
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