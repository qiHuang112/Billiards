package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qi.billiards.R
import com.qi.billiards.config.Config
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseFragment
import com.qi.billiards.util.get

class MainFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val rvMain = view.findViewById<RecyclerView>(R.id.rv_main)
        rvMain.adapter = MainAdapter(getMainItems())
        rvMain.layoutManager = LinearLayoutManager(context)
    }

    private fun getMainItems(): List<MainAdapter.MainItem> {
        return listOf(
            MainAdapter.MainItem("上次追分") {
                val game = get<ZhuiFenGame>(Config.ZhuiFen.KEY_LAST_GAME)
                if (game == null) {
                    val action = MainFragmentDirections.actionMainFragmentToZhuiFenFragment()
                    findNavController().navigate(action)
                } else {
                    val action =
                        MainFragmentDirections.actionMainFragmentToZhuiFenStartFragment(game, true)
                    findNavController().navigate(action)
                }
            },
            MainAdapter.MainItem("追分") {
                val action = MainFragmentDirections.actionMainFragmentToZhuiFenFragment()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("中八") {
                val action = MainFragmentDirections.actionMainFragmentToZhongBaFragment()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("历史记录") {
                val action = MainFragmentDirections.actionMainFragmentToHistoryFragment()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("新增玩家") {
                val action = MainFragmentDirections.actionMainFragmentToNewPlayerFragment()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("分数统计") {
                val action = MainFragmentDirections.actionMainFragmentToScoreFragment()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("附录") {
                val action = MainFragmentDirections.actionMainFragmentToAppendixFragment()
                findNavController().navigate(action)
            },
        )
    }

}