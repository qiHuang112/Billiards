package com.qi.billiards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.DialogImportItemBinding
import com.qi.billiards.databinding.FragmentImportBinding
import com.qi.billiards.http.api
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.*
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class ImportFragment : BaseBindingFragment<FragmentImportBinding>() {

    private val importItems = AppData.keys.toMutableList()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImportBinding {
        return FragmentImportBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvImport.apply {
            adapter = ImportAdapter(importItems, ::onLongClickItem)
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            launch {
                val importItem = getImportItemByDialog()
                if (importItem != null) {
                    importItems.add(0, importItem)
                    binding.rvImport.adapter?.notifyItemInserted(0)
                }
            }
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
                toast("拉取成功！")
            }
        }
    }

    private suspend fun getImportItemByDialog() = suspendCoroutine { continuation ->

        var dBinding: DialogImportItemBinding? = DialogImportItemBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        dialog.setOnDismissListener {
            continuation.safeResume(null)
            dBinding = null
        }

        dialogBinding.apply {
            tvConfirm.setOnClickListener {
                if (tvConfirm.text != "确定") {
                    toast(tvConfirm.text.toString())
                } else if (etKey.text.isBlank()) {
                    toast("请输入Key")
                } else {
                    launch {
                        tvConfirm.text = "查询中"
                        val key = etKey.text.trim().toString()
                        val (content, error) = getContentOrError(key)
                        if (error.isNotEmpty()) {
                            toast(error)
                            tvConfirm.text = "确定"
                        } else {
                            AppData.globalGames[key] = content.toMutableList()
                            continuation.safeResume(key)
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        dialog.show()
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
