package com.qi.billiards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.R
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.ui.widget.ListDialog
import com.qi.billiards.util.getBooleanByDialog
import kotlinx.coroutines.launch

/**
 * 首页
 */
class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    private var isDev = false
    private val mGameDialog by lazy { ListDialog(requireContext()) }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvRemind.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionToImport())
        }
        binding.startGame.root.setOnClickListener {
            launch {
                if (AppData.keys.isEmpty()) {
                    if (getBooleanByDialog("请至少添加一种游戏！")) {
                        val action = MainFragmentDirections.actionToSettings()
                        findNavController().navigate(action)
                    }
                } else {
                    mGameDialog.let {
                        it.setData(getGameItems())
                        it.show()
                    }
                }
            }
        }

        binding.rvMain.adapter = MainAdapter(getMainItems())
        binding.rvMain.layoutManager = LinearLayoutManager(context)
    }

    override fun onCustomResume() {

        // 如果是application启动后首次来到MainFragment的onCustomResume方法
        // 拉取一下服务器上的数据
        if (AppData.needUpdateRemoteKeyInMainFragment) {

            launch {
                try {

                    AppData.keys.forEach { key ->
                        AppData.updateRemoteSize(key)
                    }

                    if (needShowRemind()) {
                        binding.tvRemind.visibility = View.VISIBLE
                    } else {
                        binding.tvRemind.visibility = View.GONE
                    }
                    AppData.needUpdateRemoteKeyInMainFragment = false
                } catch (t: Throwable) {
                    // 请求失败 不提示
                    AppData.keys.forEach(AppData::updateRemoteSizeByAppData)
                    Log.e("MainFragment", "onCustomResume: ", t)
                }
            }

        } else {
            if (needShowRemind()) {
                binding.tvRemind.visibility = View.VISIBLE
            } else {
                binding.tvRemind.visibility = View.GONE
            }
        }
    }

    private fun needShowRemind(): Boolean {
        return AppData.keys.map { AppData.getRemoteSizeDiff(it) }.any { it > 0 }
    }

    private fun getMainItems(): List<MainAdapter.MainItem> {
        return listOf(
            MainAdapter.MainItem("历史记录", imageId = R.drawable.record) {
                val action = MainFragmentDirections.actionToHistory()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("盈亏榜单", imageId = R.drawable.profit) {
                val action = MainFragmentDirections.actionToData()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("导入历史", imageId = R.drawable.history) {
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
            MainAdapter.MainItem("设置",
                imageId = R.drawable.set,
                onLongClick = {
                    isDev = !isDev
                    binding.rvMain.adapter = MainAdapter(getMainItems())
                }
            ) {
                val action = MainFragmentDirections.actionToSettings()
                findNavController().navigate(action)
            }
        ).toMutableList().also {
            if (isDev) {
                it.add(
                    MainAdapter.MainItem("开发者功能", imageId = R.drawable.help) {
                        val action = MainFragmentDirections.actionToDev()
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }

    private fun getGameItems(): List<MainAdapter.MainItem> {
        return AppData.keys.map {
            MainAdapter.MainItem(
                name = it,
                onLongClick = {
                    launch {
                        if (getBooleanByDialog("确定删除游戏【${it}】吗？")) {
                            AppData.keys.remove(it)
                            AppData.updateKeys(AppData.keys.toMutableList())
                            mGameDialog.setData(getGameItems())
                            mGameDialog.dismiss()
                        }
                    }
                },
                onClick = {
                    mGameDialog.dismiss()
                    val action = MainFragmentDirections.actionToGame(
                        Game(GameFragment.getConfigs(), mutableListOf(), it), false
                    )
                    findNavController().navigate(action)
                },
                imageId = getGameImageId(it)
            )
        }.toMutableList()
    }

    companion object {
        fun getGameImageId(name: String): Int {
            return when (name) {
                "德州" -> R.drawable.poker
                "麻将" -> R.drawable.fa
                else -> R.drawable.heart
            }
        }
    }
}