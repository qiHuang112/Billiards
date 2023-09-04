package com.qi.billiards.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.R
import com.qi.billiards.bean.Game
import com.qi.billiards.data.AppData
import com.qi.billiards.databinding.DialogSelectKeyBinding
import com.qi.billiards.databinding.FragmentImportBinding
import com.qi.billiards.databinding.ItemSelectKeyBinding
import com.qi.billiards.http.api
import com.qi.billiards.http.apiHost
import com.qi.billiards.ui.base.BaseBindingAdapter
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.getBooleanByDialog
import com.qi.billiards.util.safeResume
import com.qi.billiards.util.toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class ImportFragment : BaseBindingFragment<FragmentImportBinding>() {

    private val importItems: MutableList<Pair<String, Int>> =
        AppData.keys.map { it to ((AppData.remoteSize[it] ?: 0) - (AppData.globalGames[it]?.size ?: 0)) }
            .toMutableList()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImportBinding {
        return FragmentImportBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvUpdateAll.setOnClickListener {
            launch {
                binding.pbImport.visibility = View.VISIBLE
                importItems.forEachIndexed { index, it ->
                    val (content, error) = getContentOrError(it.first)
                    if (error.isNotEmpty()) {
                        getBooleanByDialog(error)
                    } else {
                        AppData.addGlobalGame(it.first, content)
                        AppData.updateRemoteSizeByAppData(it.first)
                        importItems[index] = it.first to AppData.getRemoteSizeDiff(it.first)
                        binding.rvImport.adapter?.notifyItemChanged(index)
                        toast("更新成功！")
                    }
                }
                if (!destroyed) {
                    binding.pbImport.visibility = View.GONE
                }
            }
        }

        binding.rvImport.apply {
            adapter = ImportAdapter(importItems, ::onLongClickItem)
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun onLongClickItem(position: Int) = launch {
        val importItem = importItems[position]
        val key = getKeyByDialog(importItem.first)
        if (key.isNotEmpty()) {
            val games = AppData.globalGames[key]?.map { it.copy(type = importItem.first) } ?: emptyList()
            AppData.addGlobalGame(importItems[position].first, games)
            toast("导入成功！")
        }
    }

    private suspend fun getKeyByDialog(key: String) = suspendCancellableCoroutine { continuation ->

        var dBinding: DialogSelectKeyBinding? = DialogSelectKeyBinding.inflate(LayoutInflater.from(context))
        val dialogBinding = dBinding!!
        val keys = AppData.keys.filter { it != key }
        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        var selectedKey = ""

        dialog.setOnDismissListener {
            continuation.safeResume("")
            dBinding = null
        }

        dialogBinding.apply {
            tvTitle.text = "请选择要导入到【${key}】的数据"
            rvKeys.adapter = SelectKeyAdapter(keys) {
                selectedKey = it
            }
            rvKeys.layoutManager = LinearLayoutManager(context)

            tvConfirm.setOnClickListener {
                dialog.dismiss()
                continuation.safeResume(selectedKey)
            }
            tvCancel.setOnClickListener {
                dialog.dismiss()
                continuation.safeResume("")
            }
        }

        dialog.show()
    }

    class SelectKeyAdapter(

        private val keys: List<String>,
        private val onSelected: (String) -> Unit
    ) : BaseBindingAdapter<ItemSelectKeyBinding>() {

        private var selectedPosition = -1
        override fun getBinding(parent: ViewGroup): ItemSelectKeyBinding {
            return ItemSelectKeyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }

        override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemSelectKeyBinding>, position: Int) {
            val key = keys[position]
            holder.binding.apply {
                tvKeyName.text = key
                if (selectedPosition == position) {
                    ivSelected.setImageResource(R.drawable.ic_selected)
                } else {
                    ivSelected.setImageResource(R.drawable.ic_unselected)
                }
                if (position == keys.size - 1) {
                    vDivider.visibility = View.GONE
                } else {
                    vDivider.visibility = View.VISIBLE
                }
                root.setOnClickListener {
                    onSelected(key)
                    updateState(position)
                }
            }
        }

        private fun updateState(position: Int) {
            selectedPosition = position
            notifyItemRangeChanged(0, keys.size)
        }

        override fun getItemCount() = keys.size
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
