package com.qi.billiards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.FragmentImportBinding
import com.qi.billiards.http.api
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.getBooleanByDialog
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch

class ImportFragment : BaseBindingFragment<FragmentImportBinding>() {

    private val importItems = AppData.keys.toMutableList()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImportBinding {
        return FragmentImportBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvUpdateAll.setOnClickListener {
            launch {
                importItems.forEach {
                    val (content, error) = getContentOrError(it)
                    if (error.isNotEmpty()) {
                        toast(error)
                    } else {
                        AppData.addGlobalGame(it, content)
                    }
                }
                toast("更新成功！")
            }
        }

        binding.rvImport.apply {
            adapter = ImportAdapter(importItems, ::onLongClickItem)
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun onLongClickItem(position: Int) = launch {
        val key = importItems[position]
        if (getBooleanByDialog("重新拉取${key}数据吗？")) {
            val (content, error) = getContentOrError(key)
            if (error.isNotEmpty()) {
                toast(error)
            } else {
                AppData.addGlobalGame(key, content)
                toast("应用成功！")
            }
        }
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
