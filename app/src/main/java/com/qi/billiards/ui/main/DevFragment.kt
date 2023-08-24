package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentDevBinding
import com.qi.billiards.http.api
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch

class DevFragment : BaseBindingFragment<FragmentDevBinding>() {

    private val devItems by lazy { getDevFunctionItems() }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDevBinding {
        return FragmentDevBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.tbTitle.apply {
            setTitle("开发者功能")
            setOnBackClickListener {
                findNavController().navigateUp()
            }
        }

        binding.rvFunctions.apply {
            adapter = DevAdapter(devItems)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getDevFunctionItems(): List<DevAdapter.DevItem> {
        val items = mutableListOf<DevAdapter.DevItem>()

        // 同步数据
        val syncData = AppData.keys.associateWith { AppData.getRemoteSizeDiff(it) }.map { it ->
            if (it.value == 0) {
                DevAdapter.DevItem("游戏【${it.key}】与服务器同步，无需更新")
            } else if (it.value > 0) {
                DevAdapter.DevItem("游戏【${it.key}】有${it.value}条数据更新") { status, index ->
                    when (status) {
                        0 -> toast("请求中，请稍后")
                        1 -> toast("请求已成功")
                        else -> launch {
                            devItems[index].status = 0
                            binding.rvFunctions.adapter?.notifyItemChanged(index)
                            val (content, error) = getContentOrError(it.key)
                            if (error.isNotEmpty()) {
                                devItems[index].status = 2
                                toast(error)
                            } else {
                                devItems[index].status = 1
                                AppData.addGlobalGame(it.key, content)
                                AppData.updateRemoteSizeByAppData(it.key)
                            }
                            binding.rvFunctions.adapter?.notifyItemChanged(index)
                        }
                    }
                }
            } else {
                DevAdapter.DevItem("游戏【${it.key}】有${-it.value}条数据上传") { status, index ->
                    when (status) {
                        0 -> toast("请求中，请稍后")
                        1 -> toast("请求已成功")
                        else -> launch {
                            devItems[index].status = 0
                            binding.rvFunctions.adapter?.notifyItemChanged(index)
                            try {
                                api.removeAllGames(apiHost, it.key)
                                api.addAllGame(apiHost, AppData.globalGames[it.key] ?: emptyList())
                                devItems[index].status = 1
                                AppData.updateRemoteSizeByAppData(it.key)
                            } catch (e: Exception) {
                                devItems[index].status = 2
                                toast(e.message.toString())
                            }
                            binding.rvFunctions.adapter?.notifyItemChanged(index)
                        }
                    }
                }
            }
        }

        // 删除数据
        val deleteData = AppData.keys.map {
            DevAdapter.DevItem("删除游戏【${it}】") { status, index ->
                when (status) {
                    0 -> toast("删除中，请稍后")
                    1 -> toast("请求已成功")
                    else -> launch {
                        devItems[index].status = 0
                        binding.rvFunctions.adapter?.notifyItemChanged(index)
                        try {
                            api.removeAllGames(apiHost, it)
                            devItems[index].status = 1
                            AppData.remoteSize[it] = 0
                        } catch (e: Exception) {
                            devItems[index].status = 2
                            toast(e.message.toString())
                        }
                        binding.rvFunctions.adapter?.notifyItemChanged(index)
                    }
                }
            }
        }
        var index = 0
        syncData.forEach {
            items.add(it.setIndex(index++))
        }
        deleteData.forEach {
            items.add(it.setIndex(index++))
        }
        return items
    }

    private suspend fun getContentOrError(key: String): Pair<List<Game>, String> {
        var error = ""
        var content = emptyList<Game>()
        try {
            content = api.getAll(apiHost, key).data ?: emptyList()
        } catch (t: Throwable) {
            error = t.message.toString()
        }
        return content to error
    }
}