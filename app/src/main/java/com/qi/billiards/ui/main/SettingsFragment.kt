package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentSettingsBinding
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.*

class SettingsFragment : BaseBindingFragment<FragmentSettingsBinding>() {

    private val settingsItems by lazy { getSettingsItemsKt() }
    private val games by lazy {
        (get(AppData.KEY_GAMES_KEY, "").fromJson<MutableSet<String>>() ?: mutableSetOf()).toMutableList()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tbTitle.apply {
            setTitle("设置")
            setOnBackClickListener {
                findNavController().navigateUp()
            }
            setOnMenuClickListener {
                AppData.updateKeys(games)
                AppData.needUpdateRemoteKeyInMainFragment = true
                findNavController().navigateUp()
            }
        }

        binding.rvSettings.apply {
            adapter = SettingsAdapter(settingsItems)
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            val key = "游戏${games.size + 1}"
            games.add(key)
            settingsItems.add(SettingsAdapter.SettingsItem(key, "") { config ->
                games[games.size - 1] = config
            })
            binding.rvSettings.adapter?.notifyItemInserted(games.size)

        }
    }

    private fun getSettingsItemsKt(): MutableList<SettingsAdapter.SettingsItem> {
        return mutableListOf(
            SettingsAdapter.SettingsItem(KEY_HOST, apiHost) { config ->
                save(SETTINGS_KEY + KEY_HOST, config)
            }
        ).apply {
            games.mapIndexed { index, s ->
                add(SettingsAdapter.SettingsItem("游戏${index + 1}", s) { config ->
                    games[index] = config
                })
            }
        }
    }

    companion object {
        const val SETTINGS_KEY = "settings_"
        const val KEY_HOST = "服务器地址"
    }
}