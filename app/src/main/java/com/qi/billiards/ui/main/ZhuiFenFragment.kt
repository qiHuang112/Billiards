package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.qi.billiards.R
import com.qi.billiards.ui.base.BaseFragment

class ZhuiFenFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_zhuifen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<TextView>(R.id.tv_start).setOnClickListener {
            val action = ZhuiFenFragmentDirections.actionZhuiFenFragmentToZhuiFenStartFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_new_rule).setOnClickListener {
            val action = ZhuiFenFragmentDirections.actionZhuiFenFragmentToZhuiFenNewRuleFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_history_rule).setOnClickListener {
            val action = ZhuiFenFragmentDirections.actionZhuiFenFragmentToZhuiFenHistoryRuleFragment()
            findNavController().navigate(action)
        }
        view.findViewById<TextView>(R.id.tv_new_player).setOnClickListener {
            val action = ZhuiFenFragmentDirections.actionZhuiFenFragmentToNewPlayerFragment()
            findNavController().navigate(action)
        }
    }
}