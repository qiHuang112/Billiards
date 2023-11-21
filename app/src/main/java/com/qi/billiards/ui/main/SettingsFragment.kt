package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.databinding.FragmentSettingsBinding
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.get

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding>() {
    private val settings by lazy { listOf(yybb).map { Setting(it, get(SETTINGS_KEY_ + it, false)) } }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.rvSettings.apply {
            adapter = SettingsAdapter(settings)
            layoutManager = LinearLayoutManager(context)
        }
    }


}