package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.game.DeGame
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.copyToClipboard
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
            MainAdapter.MainItem("开始") {
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
        )
    }

    private fun exportData() {
        launch {
            val data = DbUtil.getAllGames().toJson()
            copyToClipboard(data)
            toast("本地数据已复制到剪切板")
        }
    }

}