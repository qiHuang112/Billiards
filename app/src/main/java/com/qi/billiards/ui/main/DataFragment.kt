package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentDataBinding
import com.qi.billiards.ui.base.BaseBindingFragment

/**
 * 玩家列表
 */
class DataFragment : BaseBindingFragment<FragmentDataBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDataBinding {
        return FragmentDataBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        binding.tlTab.setupWithViewPager(binding.vpContent)

        binding.vpContent.apply {
            adapter = DataPagerAdapter(getFragments(), childFragmentManager)
        }

    }

    private fun getFragments() = AppData.keys.map {
        PlayerFragment(it)
    }
}