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

    private val devItems by lazy { getDefaultDevItems() }
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

    private fun getDefaultDevItems(): List<DevAdapter.DevItem> {
        val removeItems = AppData.keys.mapIndexed { index, key ->
            DevAdapter.DevItem("removeAllGames/${key}") { status ->
                if (status == 0) { // 请求中
                    toast("请求中，请稍后")
                } else {
                    launch {
                        try {
                            devItems[index + 2].status = 0
                            binding.rvFunctions.adapter?.notifyItemChanged(index + 2)
                            api.removeAllGames(apiHost, key)
                            devItems[index + 2].status = 1
                        } catch (e: Exception) {
                            devItems[index + 2].status = 2
                        }
                        binding.rvFunctions.adapter?.notifyItemChanged(index + 2)
                    }
                }
            }
        }

        return mutableListOf(
            DevAdapter.DevItem("addGame") { status ->
                if (status == 0) { // 请求中
                    toast("请求中，请稍后")
                } else {
                    launch {
                        try {
                            devItems[0].status = 0
                            binding.rvFunctions.adapter?.notifyItemChanged(0)
                            getUnUploadedGames().map { game ->
                                api.addGame(apiHost, game)
                            }
                            devItems[0].status = 1
                        } catch (e: Exception) {
                            devItems[0].status = 2
                        }
                        binding.rvFunctions.adapter?.notifyItemChanged(0)
                    }
                }
            },
            DevAdapter.DevItem("addAllGame") { status ->
                if (status == 0) { // 请求中
                    toast("请求中，请稍后")
                } else {
                    launch {
                        try {
                            devItems[1].status = 0
                            binding.rvFunctions.adapter?.notifyItemChanged(1)
                            api.addAllGame(apiHost, AppData.globalGames.flatMap { it.value })
                            devItems[1].status = 1
                        } catch (e: Exception) {
                            devItems[1].status = 2
                        }
                        binding.rvFunctions.adapter?.notifyItemChanged(1)
                    }
                }
            }
        ).apply {
            addAll(removeItems)
        }
    }

    private fun getUnUploadedGames(): List<Game> {
        return mutableListOf()
    }
}