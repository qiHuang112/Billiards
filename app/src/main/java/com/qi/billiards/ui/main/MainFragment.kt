package com.qi.billiards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.getBooleanByDialog
import kotlinx.coroutines.launch

/**
 * 首页
 */
class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvRemind.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionToImport())
        }
        binding.rvMain.adapter = MainAdapter(getMainItems())
        binding.rvMain.layoutManager = LinearLayoutManager(context)
    }

    override fun onCustomResume() {

        launch {
            if (AppData.keys.isEmpty()) {
                if (getBooleanByDialog("请至少添加一种游戏！")) {
                    val action = MainFragmentDirections.actionToSettings()
                    findNavController().navigate(action)
                }
            } else if (AppData.keyUpdated) {
                binding.rvMain.adapter = MainAdapter(getMainItems())
                AppData.keyUpdated = false
            }
        }

        // 如果是application启动后首次来到MainFragment的onCustomResume方法
        // 拉取一下服务器上的数据
        if (AppData.needUpdateRemoteKeyInMainFragment) {

            launch {
                try {

                    AppData.keys.forEach { key ->
                        AppData.updateRemoteSize(key)
                    }
                    val needShowRemind = AppData.remoteSize.any { (key, size) ->
                        size != (AppData.globalGames[key]?.size ?: 0)
                    }

                    if (needShowRemind) {
                        binding.tvRemind.visibility = View.VISIBLE
                    } else {
                        binding.tvRemind.visibility = View.GONE
                    }
                    AppData.needUpdateRemoteKeyInMainFragment = false
                } catch (t: Throwable) {
                    Log.e("MainFragment", "onCustomResume: ", t)
                }
            }

        } else {
            if (AppData.remoteSize.any { (key, size) ->
                    size != (AppData.globalGames[key]?.size ?: 0)
                }) {
                binding.tvRemind.visibility = View.VISIBLE
            } else {
                binding.tvRemind.visibility = View.GONE
            }
        }
    }

    private fun getMainItems(): List<MainAdapter.MainItem> {

        return AppData.keys.map {
            MainAdapter.MainItem(it) {
                val action = MainFragmentDirections.actionToGame(
                    Game(GameFragment.getConfigs(), mutableListOf(), it), false
                )
                findNavController().navigate(action)
            }
        }.toMutableList().apply {
            addAll(
                listOf(
                    MainAdapter.MainItem("历史记录") {
                        val action = MainFragmentDirections.actionToHistory()
                        findNavController().navigate(action)
                    },
                    MainAdapter.MainItem("盈亏榜单") {
                        val action = MainFragmentDirections.actionToData()
                        findNavController().navigate(action)
                    },
                    MainAdapter.MainItem("导入历史") {
                        if (apiHost.isBlank()) {
                            launch {
                                if (getBooleanByDialog("请先配置服务器地址")) {
                                    val action = MainFragmentDirections.actionToSettings()
                                    findNavController().navigate(action)
                                }
                            }
                        } else {
                            val action = MainFragmentDirections.actionToImport()
                            findNavController().navigate(action)
                        }
                    },
                    MainAdapter.MainItem("设置") {
                        val action = MainFragmentDirections.actionToSettings()
                        findNavController().navigate(action)
                    },
                    MainAdapter.MainItem("开发者功能") {
                        val action = MainFragmentDirections.actionToDev()
                        findNavController().navigate(action)
                    }
                )
            )
        }
    }

}