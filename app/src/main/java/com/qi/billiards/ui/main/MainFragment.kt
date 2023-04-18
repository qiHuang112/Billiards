package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.game.ZhuiFenGame
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.get

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