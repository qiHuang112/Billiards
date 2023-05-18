package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.FragmentMainBinding
import com.qi.billiards.ui.base.BaseBindingFragment

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
            MainAdapter.MainItem("追分") {
                val action = MainFragmentDirections.actionToZhuiFenConfig()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("中八") {
                val action = MainFragmentDirections.actionToZhongBa()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("历史记录") {
                val action = MainFragmentDirections.actionToHistory()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("玩家列表") {
                val action = MainFragmentDirections.actionToNewPlayer()
                findNavController().navigate(action)
            },
            MainAdapter.MainItem("附录") {
                val action = MainFragmentDirections.actionToAppendix()
                findNavController().navigate(action)
            },
        )
    }

}