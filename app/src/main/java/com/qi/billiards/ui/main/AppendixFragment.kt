package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.FragmentAppendixBinding
import com.qi.billiards.ui.base.BaseBindingFragment

class AppendixFragment : BaseBindingFragment<FragmentAppendixBinding>() {
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAppendixBinding {
        return FragmentAppendixBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.tvTitle.text = "规则总览"
        binding.rvAppendix.apply {
            adapter = AppendixAdapter(getAppendixItems())
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getAppendixItems(): List<AppendixAdapter.AppendixItem> {
        return listOf(
            AppendixAdapter.AppendixItem("追分规则", Config.TYPE_ZHUI_FEN) {
                val action = AppendixFragmentDirections.actionToRuleMaking(Config.TYPE_ZHUI_FEN.toString())
                findNavController().navigate(action)
            },
            AppendixAdapter.AppendixItem("中八规则", Config.TYPE_ZHONG_BA) {
                val action = AppendixFragmentDirections.actionToRuleMaking(Config.TYPE_ZHONG_BA.toString())
                findNavController().navigate(action)
            },
        )
    }
}