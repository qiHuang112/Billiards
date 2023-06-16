package com.qi.billiards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config.dividerKey
import com.qi.billiards.databinding.DialogImportItemBinding
import com.qi.billiards.databinding.FragmentImportBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.GameEntity
import com.qi.billiards.db.PlayerEntity
import com.qi.billiards.http.api
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.*
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class ImportFragment : BaseBindingFragment<FragmentImportBinding>() {

    private val importItems = getImportItems()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImportBinding {
        return FragmentImportBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvImport.apply {
            adapter = ImportAdapter(importItems) {
                applyItem(importItems[it])
            }
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            launch {
                val importItem = getImportItemByDialog()
                if (importItem != null) {
                    importItems.add(0, importItem)
                    binding.rvImport.adapter?.notifyItemInserted(0)
                    saveImportItems()
                }
            }
        }
    }

    private fun applyItem(item: ImportAdapter.ImportItem) {
        try {
            val arr = item.content!!.split(dividerKey)
            val players = arr[0].fromJson<Array<PlayerEntity>>()!!
            val games = arr[1].fromJson<Array<GameEntity>>()!!
            launch {
                DbUtil.deleteAllPlayers()
                DbUtil.deleteAllGames()
                DbUtil.addPlayers(*players)
                DbUtil.addGames(*games)
                toast("应用成功")
            }
        } catch (t: Throwable) {
            toast("应用失败")
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
                } else if (etKey.text.isBlank() && etContent.text.isBlank()) {
                    toast("请输入Key或Content")
                } else if (etContent.text.isBlank()) {
                    launch {
                        tvConfirm.text = "查询中"
                        val key = etKey.text.trim().toString()
                        val (content, error) = getContentOrError(key)
                        if (error.isNotEmpty()) {
                            toast(error)
                            tvConfirm.text = "确定"
                        } else if (judgeContent(content)) {
                            continuation.safeResume(ImportAdapter.ImportItem(key, content))
                            dialog.dismiss()
                        } else {
                            toast("数据错误！")
                            tvConfirm.text = "确定"
                        }
                    }
                } else {
                    val content = etContent.text.toString()
                    val key = "fromContent-${System.currentTimeMillis()}"
                    if (judgeContent(content)) {
                        continuation.safeResume(ImportAdapter.ImportItem(key, content))
                        dialog.dismiss()
                    } else {
                        toast("数据错误！")
                        tvConfirm.text = "确定"
                    }
                }
            }
        }

        dialog.show()
    }

    private suspend fun getContentOrError(key: String): Pair<String, String> {
        var error = ""
        var content = ""
        try {
            content = api.getHistory(key).string()
        } catch (t: Throwable) {
            error = t.message.toString()
        }
        return content to error
    }

    private fun judgeContent(content: String): Boolean {
        if (content.isBlank()) {
            return false
        }
        val arr = content.split(dividerKey)
        if (arr.size != 2) {
            return false
        }
        var isOk = true
        try {
            arr[0].fromJson<Array<PlayerEntity>>()
            arr[1].fromJson<Array<GameEntity>>()
        } catch (t: Throwable) {
            isOk = false
        }
        return isOk
    }

    private fun getImportItems(): MutableList<ImportAdapter.ImportItem> {
        return get(KEY_IMPORT_ITEMS, "")
            ?.fromJson<Array<ImportAdapter.ImportItem>>()
            ?.toMutableList()
            ?: mutableListOf()
    }

    private fun saveImportItems() {
        save(KEY_IMPORT_ITEMS, importItems.toJson())
    }

    companion object {
        const val KEY_IMPORT_ITEMS = "KEY_IMPORT_ITEMS"
    }
}
