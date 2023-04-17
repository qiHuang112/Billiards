package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.qi.billiards.R
import com.qi.billiards.config.Config
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseFragment
import com.qi.billiards.util.get

class MainFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tv_zhui_fen_last).setOnClickListener {
            val game = get<ZhuiFenGame>(Config.ZhuiFen.KEY_LAST_GAME)
            if (game == null) {
                val action = MainFragmentDirections.actionMainFragmentToZhuiFenFragment()
                findNavController().navigate(action)
            } else {
                val action = MainFragmentDirections.actionMainFragmentToZhuiFenStartFragment(game)
                findNavController().navigate(action)
            }
        }
        view.findViewById<TextView>(R.id.tv_zhui_fen).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToZhuiFenFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_zhong_ba).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToZhongBaFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_history).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToHistoryFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_new_player).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToNewPlayerFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_score).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToScoreFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_appendix).setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAppendixFragment()
            findNavController().navigate(action)
        }
    }

}