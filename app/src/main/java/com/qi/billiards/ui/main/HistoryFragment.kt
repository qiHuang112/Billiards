package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qi.billiards.databinding.FragmentHistoryBinding
import com.qi.billiards.ui.base.BaseBindingFragment

/**
 * 历史对局
 */
class HistoryFragment : BaseBindingFragment<FragmentHistoryBinding>() {

    private var clickedPos = -1

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
    }

    override fun onCustomResume() {
        if (clickedPos >= 0) {
        }
    }

    private fun jumpToDetail(position: Int) {
//        findNavController().navigate(action)
    }
}