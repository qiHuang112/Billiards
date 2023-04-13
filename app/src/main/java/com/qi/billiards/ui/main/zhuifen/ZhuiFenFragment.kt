package com.qi.billiards.ui.main.zhuifen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.ui.base.BaseFragment

class ZhuiFenFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_zhuifen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<TextView>(R.id.tv_start).setOnClickListener {
            val action = ZhuiFenFragmentDirections.actionZhuiFenFragmentToZhuiFenStartFragment()
            findNavController().navigate(action)
        }

        val rvCurrentRule = view.findViewById<RecyclerView>(R.id.rv_current_rule)
        rvCurrentRule.adapter = RuleAdapter(getDefaultRules())
        rvCurrentRule.layoutManager = LinearLayoutManager(context)

        val rvCurrentPlayer = view.findViewById<RecyclerView>(R.id.rv_current_player)
        rvCurrentPlayer.adapter = PlayerAdapter(getLatestPlayers())
        rvCurrentPlayer.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        private fun getDefaultRules(): List<Rule> {
            return listOf(
                Rule("初始分数", "25"),
                Rule("犯规罚分", "1"),
                Rule("普胜得分", "4"),
                Rule("小金得分", "7"),
                Rule("大金得分", "7"),
                Rule("基数", "5"),
            )
        }

        private fun getLatestPlayers(): MutableList<Player> {
            return mutableListOf(
                Player("黄麒"),
                Player("李逸波"),
                Player("辛宇"),
            )
        }

        data class Rule(
            var name: String,
            var score: String,
        )

        data class Player(
            var name: String
        )
    }
}