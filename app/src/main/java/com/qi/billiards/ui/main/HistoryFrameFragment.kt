package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentHistoryFrameBinding
import com.qi.billiards.ui.base.BaseBindingFragment

class HistoryFrameFragment : BaseBindingFragment<FragmentHistoryFrameBinding>() {
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryFrameBinding {
        return FragmentHistoryFrameBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.tlTab.setupWithViewPager(binding.vpContent)

        binding.vpContent.apply {
            adapter = HistoryPagerAdapter(getFragments(), childFragmentManager)
        }
    }

    private fun getFragments() = AppData.keys.map {
        HistoryFragment(it)
    }

}